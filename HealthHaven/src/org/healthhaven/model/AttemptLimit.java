package org.healthhaven.model;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;



public class AttemptLimit {
	
	private static final int maxAttempts = 3;
	
	//ChatGPT
	public static List<Long> timesWithinLastHour(List<Long> timestamps) {
        List<Long> withinLastHour = new ArrayList<>();
        long currentTime = Instant.now().getEpochSecond();

        for (Long timestamp : timestamps) {
            if (timestamp > currentTime - 3600 && timestamp <= currentTime) {
                withinLastHour.add(timestamp);
            }
        }

        return withinLastHour;
    }
	
	
	public static List<List<Long>> withinAttemptLimit(List<Long> timestamps) {
		List<Long> lastHour = timesWithinLastHour(timestamps);
		List<Long> allowAttempt = new ArrayList<>();
		
		List<List<Long>> rList = new ArrayList<>();
		
		if (lastHour.size() >= maxAttempts){
			allowAttempt.add(0L);
			rList.add(allowAttempt);
			rList.add(lastHour);
			return rList;
		}
		
		else {
			long currentTime = Instant.now().getEpochSecond();
			allowAttempt.add(1L);
			rList.add(allowAttempt);
			lastHour.add(currentTime);
			rList.add(lastHour);
			return rList;
		}
	}
	
	
	public static String tListtoString(List<Long> tList) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < tList.size(); i++) {
			sb.append(tList.get(i));
			sb.append(",");
		}
		String rString = sb.toString();
		rString.substring(0, (rString.length() - 1));
		return rString;		
	}
	
	
	//Inspired by ChatGPT
	public static List<Long> StringtotList(String str) {
		List<Long> tList = new ArrayList<>();
		String[] parse1 = str.split(",");
		for(String elem:parse1) {
			tList.add(Long.parseLong(elem));
		}
		return tList;
	}

	
	
	
		
	}
	


