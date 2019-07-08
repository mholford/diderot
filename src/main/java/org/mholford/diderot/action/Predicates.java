package org.mholford.diderot.action;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class Predicates {
  
  public static List<String> get(String value) {
    List<String> output = new ArrayList<>();
    output.add(value);
    
    // this can evolve into something externalized.
    switch (value) {
      case "place":
        output.add("location");
        break;
      case "inventor":
        output.addAll(Lists.newArrayList("inventors", "invented_by"));
        break;
      case "creator":
        output.addAll(Lists.newArrayList("creators"));
        break;
      case "author":
        output.addAll(Lists.newArrayList("authors", "writer", "writers", "composer"));
        break;
      case "director":
        output.addAll(Lists.newArrayList("directors"));
        break;
      case "architect":
        output.addAll(Lists.newArrayList("architect_or_builder", "architecture_firm", "architects"));
        break;
      case "founder":
        output.addAll(Lists.newArrayList("founders", "founded_by"));
        break;
      case "education":
        output.addAll(Lists.newArrayList("alma_mater"));
        break;
      case "leader_name1":
        output.addAll(Lists.newArrayList("premier", "governor"));
        break;
      case "largestcity":
        output.addAll(Lists.newArrayList("largest_city"));
        break;
      case "length":
        output.addAll(Lists.newArrayList("elevation", "elevation_m", "elevation_ft", "height_ft", "height_in",
            "height_metric", "height_m", "height_imperial", "heightft", "heightinch", "height_cm", "heightm",
            "heights", "heightin"));
        break;
      case "population":
        output.addAll(Lists.newArrayList("population_total"));
        break;
      default:
        break;
    }
    
    return output;
  }
}
