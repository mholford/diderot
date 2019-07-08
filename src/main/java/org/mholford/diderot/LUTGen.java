package org.mholford.diderot;

import org.mholford.chatlantis.Utils;
import org.mholford.chatlantis.lookup.*;
import org.mholford.chatlantis.lookup.instruction.Instruction;
import org.mholford.chatlantis.lookup.instruction.SetIntent;
import org.mholford.chatlantis.lookup.instruction.SetStringSlot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.mholford.chatlantis.lookup.GenAttr.OPTIONAL;

public class LUTGen implements LUTGenerator, Utils {
  private String outputFile = "/home/matt/projects/diderot/src/main/resources/diderot-gen-LUT.csv";
  
  @Override
  public void generate() throws IOException {
    LookupUtil lu = new LookupUtil();
    Map<String, String> allPerms = new TreeMap<>();
    List<String> whereVariants = listOf("where", "from where", "in what place", "in what location");
    List<String> beVariants = listOf("is", "was", "are", "were");
    List<String> birthplaceVariants = listOf("from", "born", "birth place", "birthplace");
    List<String> whenVariants = listOf("when", "at what time", "on what date");
    List<String> birthdateVariants = listOf("born", "birthday", "date of birth");
  
    ClauseConf whereBorn = new ClauseConf("whereBorn")
        .addWildcard(false, OPTIONAL)
        .addPhrase("where", whereVariants)
        .addPhrase("be", beVariants)
        .addWildcard(true)
        .addPhrase("birthplace", birthplaceVariants);
  
    ClauseConf whenBorn = new ClauseConf("whenBorn")
        .addWildcard(false, OPTIONAL)
        .addPhrase("when", whenVariants)
        .addPhrase("be", beVariants)
        .addWildcard(true)
        .addPhrase("birthdate", birthdateVariants);
    
    allPerms.putAll(lu.createLookupEntries(whereBorn, getResultFromObj("birth_place")));
    allPerms.putAll(lu.createLookupEntries(whenBorn, getResultFromObj("birth_date")));
    
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
      for (Map.Entry<String, String> e : allPerms.entrySet()) {
        bw.write(fmt("%s,%s\n", e.getKey(), e.getValue()));
        bw.flush();
      }
    }
  }
  
  private List<Instruction> getResultFromObj(String pred) {
    return listOf(new SetIntent("answerQuestion"),
        new SetStringSlot("/objects/query.subj", new WildcardMarker()),
        new SetStringSlot("/objects/query.pred", pred),
        new SetStringSlot("/objects/query.obj", "ANY"),
        new SetStringSlot("/objects/query.answerField", "OBJ"));
  }
  
  public static void main(String[] args) {
    try {
      new LUTGen().generate();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
