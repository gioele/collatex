package eu.interedition.collatex.rest;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.common.collect.Lists;

import eu.interedition.collatex.input.Witness;
import eu.interedition.collatex.input.Word;

public class ParserResource extends ServerResource {

  private static final MediaType[] TYPES = { MediaType.APPLICATION_JSON };

  public ParserResource() {
    getVariants().put(Method.POST, Arrays.asList(TYPES));
  }

  @Post
  public Representation acceptItem(Representation entity) {
    System.err.println("Handeling POST!");

    Form form = new Form(entity);
    String firstValue = form.getFirstValue("request");

    List<Word> words = Lists.newArrayList();
    int position = 1;

    JsonRepresentation jsonRepresentation;
    try {
      jsonRepresentation = new JsonRepresentation(firstValue);
      JSONArray jsonArray = jsonRepresentation.getJsonArray();
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        String token = jsonObject.getString("token");
        Word word = new Word("witnessid", token, position);
        position++;
        words.add(word);
      }
      //    } catch (IOException e) {
      //      e.printStackTrace();
      //      throw new RuntimeException(e);
    } catch (JSONException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    Witness witness = new Witness("witnessid", words);
    Representation representation = new StringRepresentation(witness.toString(), MediaType.TEXT_PLAIN);
    // Representation representation = null;
    return representation;
  }
}