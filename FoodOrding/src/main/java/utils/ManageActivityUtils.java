package utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ManageActivityUtils {
   private static  List<Activity> activities=new ArrayList<Activity>();
   public static void addActivity(Activity activity){
	   activities.add(activity);
   }
   public void removeActivity(Activity activity){
	   activities.remove(activity);
   }
   public static void finishAll(){
	   for(Activity activity:activities){
		   if(!activity.isFinishing()){
			   activity.finish();
		   }
	   }
   }
}
