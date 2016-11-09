package language;

import java.util.HashMap;

public class TranslateConstants 
{	
	private static TranslateConstants translateConstants = null;
	
	public static final String earthquake = "ভূমিকম্প";
	HashMap<String, String> translate = new HashMap<>();
	
	private TranslateConstants() 
	{
		translate.put("CATEGORY","ক্যাটাগরি");
		translate.put("CATEGORY_PLURAL","ক্যাটাগরির");
		
		translate.put("equipment","ইকুইপম্যান্ট");
		translate.put("equipment_plural","ইকুইপম্যান্টের");
		
		translate.put("supplies","সাপ্লাই");
		translate.put("supplies_plural","সাপ্লাইর");
		
		translate.put("asset","এসেট");
		translate.put("asset_plural","এসেটের");
		
		translate.put("transportation","যানবাহন");
		translate.put("transportation_plural","যানবাহনের");
		
		translate.put("unavailable","অপ্রাপ্য");
		translate.put("available","সুলভ");
		translate.put("expired","মেয়াদোত্তীর্ণ");	
		
		translate.put("requested","রিকুইজিশন করা হয়েছে");
		translate.put("accepted","রিকুইজিশন গৃহীত");
		translate.put("sent","পাঠানো হয়েছে");
		translate.put("declined","বাতিল");
		
	}
	
	private synchronized static void createCategoryRepository()
	{
	    if (translateConstants == null)
	    {
	    	translateConstants = new TranslateConstants();
	    }
	}
	
	public static TranslateConstants getInstance()
    {
	    if (translateConstants == null)
	    {
	    	createCategoryRepository();
	    }
	    return translateConstants;
    }
	
	public String getTranslate(String key)
	{
		return translate.get(key);
	}
}
