package org.mholford.diderot.action;

import com.google.common.base.Strings;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.action.Action;
import org.mholford.chatlantis.bot.Bot;
import org.mholford.chatlantis.context.FullContext;
import org.mholford.chatlantis.lookup.instruction.Instruction;
import org.mholford.chatlantis.lookup.instruction.SetStringSlot;

import java.util.List;
import java.util.Map;

public class SolrQueryAction implements Action, Utils {
  public static final String SOLR_ADDRESS = "solrAddress";
  public static final String NOT_FOUND = "Sorry, I could not find the answer";
  public static final String ANY = "ANY";
  private SolrClient solr;
  
  @Override
  public List<Instruction> act(FullContext ctx, Bot bot) {
    SolrQuery sq = formSolrQuery(ctx);
    String spoken;
    try {
      QueryResponse resp = solr.query(sq);
      if (resp.getResults().size() > 0) {
        SolrDocument doc = resp.getResults().get(0);
        String answer = answerFromDoc(doc, ctx);
        if (answer == null) {
          spoken = NOT_FOUND;
        } else {
          spoken = answer;
        }
      } else {
        spoken = NOT_FOUND;
      }
    } catch (Exception e) {
      spoken = "Something went wrong while answering your question";
    }
    return listOf(new SetStringSlot("/action.spoken", spoken));
  }
  
  @Override
  public void init(Map<String, String> map) {
    String solrAddr = map.get(SOLR_ADDRESS);
    solr = new HttpSolrClient.Builder(solrAddr).build();
  }
  
  private SolrQuery formSolrQuery(FullContext ctx) {
    String subj = (String) ctx.get("$utt:/objects/query.subj");
    String pred = (String) ctx.get("$utt:/objects/query.pred");
    String obj = (String) ctx.get("$utt:/objects/query.obj");
    
    StringBuilder sb = new StringBuilder();
    sb.append("{!boost b=max(1,log(pop))}");
    if (!Strings.isNullOrEmpty(subj) && !subj.equals(ANY)) {
      // TODO:  THIS IS WRONG!! Fix ingest
      subj = subj.replaceAll(" ", "_");
      sb.append(fmt("+subj:\"%s\"", subj));
    }
    if (!Strings.isNullOrEmpty(pred) && !pred.equals(ANY)) {
      sb.append(fmt("+pred:%s", expandPredicate(pred)));
    }
    if (!Strings.isNullOrEmpty(obj) && !obj.equals(ANY)) {
      sb.append(fmt("+obj:\"%s\"", obj));
    }
    
    SolrQuery sq = new SolrQuery(sb.toString());
    sq.setParam("rows", "1");
    sq.setParam("fl", "subj obj pred type");
    return sq;
  }
  
  private String expandPredicate(String pred) {
    List<String> addlPred = Predicates.get(pred);
    StringBuilder sb = new StringBuilder(enquote(pred));
    for (String p : addlPred) {
      sb.append(" OR " + enquote(p));
    }
    return sb.toString();
  }
  
  private String enquote(String orig) {
    return fmt("\"%s\"", orig);
  }
  
  private String answerFromDoc(SolrDocument doc, FullContext ctx) {
    String answerField = (String) ctx.get("$UTT:/objects/query.answerField");
    switch (answerField) {
      case "OBJ":
        return doc.getFirstValue("obj").toString().trim();
      case "SUBJ":
        return doc.getFirstValue("subj").toString().trim();
      case "PRED":
        return doc.getFirstValue("pred").toString().trim();
      default:
        return null;
    }
  }
}
