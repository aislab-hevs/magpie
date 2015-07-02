package ch.hevs.aislab.magpie.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import ch.hevs.aislab.magpie.environment.Services;

public class AgentFactory {

	public static MagpieAgent createSimplePrologMagpie(
			Context context,
			String prologFile) {	
		MagpieAgent agent = new MagpieAgent("PrologAgent", Services.LOGIC_TUPLE, Services.REST_CLIENT, Services.RULE_SET);
		String prologCode = readTextFile(context, prologFile);
		PrologAgentMind mind = new PrologAgentMind(prologCode);
		agent.setMind(mind);
		return agent;	
	}
	
	private static String readTextFile(Context context, String fileName) {
		try {
			InputStream is = context.getAssets().open(fileName);
			
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			
			return sb.toString();
	
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
}
