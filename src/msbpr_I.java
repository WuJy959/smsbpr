import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.*;
import java.util.Map.Entry;


public class msbpr_I
{
	// === Configurations
	// the number of latent dimensions
	public static int d ;
    // 
	public static int topK ; // top k in evaluation
	// tradeoff $\alpha_u$
	public static float alpha_u = 0.01f;
	// tradeoff $\alpha_v$
    public static float alpha_v = 0.01f;
    // tradeoff $\beta_v$
    public static float beta_v = 0.01f;
    // learning rate $\gamma$
    public static float gamma = 0.01f;
	// Model harmonic parameters
	public static float lambda = 0.5f;
    
    // 交互物品用户个数阈值
    public static int threshold_c ;
    // 物品相似度阈值
    public static float threshold_i ;
	// 用户相似度阈值
	public static float threshold_u ;
    // 交互次数阈值
    public static float threshold_c2 ;

    // msbpr_u 的用户相似度阈值
    public static float threshold_user ;

    // 用于判断是哪个数据集
	public static String filename;

    // 储存物品相似度矩阵文件名
    public static String fnSimilarity_i ;
    // 储存用户相似度矩阵文件名
    public static String fnSimilarity_u ;
    // 储存物品-用户相似度矩阵文件名
    public static String fnSimilarity_i_u ;
    // 储存结果文件名
    public static String fnresult_evaluation;
    
    
    // === Input data files
     // -- 30music-5k5k(n=4586 m=4989)
    // public static String fnTrainData = "/home/java/code/Java/MBPR/data/30music/30music-5k5k-train";
    // public static String fnTestData = "/home/java/code/Java/MBPR/data/30music/30music-5k5k-test";
    // public static String fnValidData = "/home/java/code/Java/MBPR/data/30music/30music-5k5k-valid";
	// public static int n = 4586 ; // number of users
	// public static int m = 4989 ; // number of items
	 
	    // -- tmall-5k5k(n=4516 m=5000) 
    // public static String fnTrainData = "/home/java/code/Java/MBPR/data/Tmall/tmall-5k5k-train";
    // public static String fnTestData = "/home/java/code/Java/MBPR/data/Tmall/tmall-5k5k-test";
    // public static String fnValidData = "/home/java/code/Java/MBPR/data/Tmall/tmall-5k5k-valid";
    // public static int n = 4516 ; // number of users
    // public static int m = 5000 ; // number of items
 
		// -- rsc15-5k5k(n=4926 m=4997)
	// public static String fnTrainData = "/home/java/code/Java/MBPR/data/rsc15/rsc15-5k5k-train";
	// public static String fnTestData = "/home/java/code/Java/MBPR/data/rsc15/rsc15-5k5k-test";
	// public static String fnValidData = "/home/java/code/Java/MBPR/data/rsc15/rsc15-5k5k-valid";
	// public static int n = 4926 ; // number of users
	// public static int m = 4997 ; // number of items

	// -- aotm -5k5k(n=4885  m=5000)
	public static String fnTrainData = "/home/java/code/Java/MBPR/data/aotm/aotm-5k5k-train";
	public static String fnTestData = "/home/java/code/Java/MBPR/data/aotm/aotm-5k5k-test";
	public static String fnValidData = "/home/java/code/Java/MBPR/data/aotm/aotm-5k5k-valid";
    public static int n = 4885 ; // number of users
	public static int m = 5000 ; // number of items

	 	//-- yelp -5k5k(n=4978  m=5000)
	// public static String fnTrainData = "/home/java/code/Java/MBPR/data/yelp/yelp-5k5k-train";
	// public static String fnTestData = "/home/java/code/Java/MBPR/data/yelp/yelp-5k5k-test";
	// public static String fnValidData = "/home/java/code/Java/MBPR/data/yelp/yelp-5k5k-valid";
    // public static int n = 4978 ; // number of users
	// public static int m = 5000 ; // number of items
	 
	    // -- Last.fm-5k5k 
//		 public static String fnTrainData = "/home/java/code/Java/MBPR/data/last.fm/last.fm-5k5k-train";
//		 public static String fnTestData = "/home/java/code/Java/MBPR/data/last.fm/last.fm-5k5k-test";
//		 public static String fnValidData = "/home/java/code/Java/MBPR/data/last.fm/last.fm-5k5k-valid";
//		 public static int n = 1640 ; // number of users
//		 public static int m = 12440 ; // number of items
	// 
	public static int num_iterations = 100000; // scan number over the whole data 
	
	// select the similarity model
	public static boolean flagsimilarity_i = false;
	public static boolean flagsimilarity_u = false;
	public static boolean flagsimilarity_i_u = false;
	
	// select the split model
	public static boolean flagsplit_i ;
	public static boolean flagsplit_u ;
	public static boolean flagsplit_i_u ;
    public static boolean flagsplit_user ;
	
	// valid & test
	public static boolean flagvalid = true;
	public static boolean flagtest = false;
	
	// === Evaluation
    //	
	public static boolean flagMRR = true;
	public static boolean flagMAP = true;
	public static boolean flagARP = false;
	public static boolean flagAUC = false;
	
	// average value
	public static float Pre_ave = 0;
	public static float Rec_ave = 0;
	public static float F1_ave = 0;
	public static float NDCG_ave = 0;
	public static float Onecall_ave = 0;
	public static float MRR_ave = 0;
	public static float MAP_ave = 0;
	public static float ARP_ave = 0;
	public static float AUC_ave = 0;

	// === Data
	public static HashMap<Integer, HashMap<Integer, Integer>> Data = new HashMap<Integer, HashMap<Integer,Integer>>();
	public static HashMap<Integer, HashMap<Integer, Integer>> DataItem2User = new HashMap<Integer, HashMap<Integer,Integer>>(); // 交互同一物品的所有用户及相应的次数

    // === training data
    public static HashMap<Integer, HashMap<Integer, Integer>> TrainData = new HashMap<Integer, HashMap<Integer,Integer>>(); // 一个用户交互过的所有物品及相应的次数
    public static HashMap<Integer, HashMap<Integer, Integer>> TrainDataItem2User = new HashMap<Integer, HashMap<Integer,Integer>>(); // 交互同一物品的所有用户及相应的次数

	// === Iu_p data
	public static HashMap<Integer, List<Integer>> Iu_p_data = new HashMap<Integer, List<Integer>>();

	// === Iu_uk data
	public static HashMap<Integer, List<Integer>> Iu_uk_data = new HashMap<Integer, List<Integer>>();

	// === Iu_j data
	public static HashMap<Integer, List<Integer>> Iu_j_data = new HashMap<Integer, List<Integer>>();

    // === Ui_l data
	public static HashMap<Integer, List<Integer>> Ui_l_data = new HashMap<Integer, List<Integer>>();

	// === Ui_v data
	public static HashMap<Integer, List<Integer>> Ui_v_data = new HashMap<Integer, List<Integer>>();

    // === test data
    public static HashMap<Integer, HashSet<Integer>> TestData = new HashMap<Integer, HashSet<Integer>>(); 
    
    // === validation data
    public static HashMap<Integer, HashSet<Integer>> ValidData = new HashMap<Integer, HashSet<Integer>>();
        
    // === whole item set
    public static HashSet<Integer> ItemSetWhole = new HashSet<Integer>();
    
    // === whole user set
    public static HashSet<Integer> UserSetWhole = new HashSet<Integer>();
    
    // === The item that most users may dislike
    public static HashSet<Integer> ItemSetDislike = new HashSet<Integer>();
    
    // === some statistics, start from index "0"
    public static int[] itemRatingNumTrain; 
    public static int[] userRatingNumTrain;
    
    // === model parameters to learn, start from index "0"
    public static float[][] U;
    public static float[][] V;
    public static float[] biasV;  // bias of item
    public static float[] biasU;  // bias of user
        
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void main(String[] args) throws Exception
    {	
    	// === Print the configurations
		System.out.println("-------" + "我的模型" + "-------");
		
		System.out.println("（1）参数设置：");
    	System.out.println("d: " + Integer.toString(d));
    	System.out.println("alpha_u: " + Float.toString(alpha_u));
    	System.out.println("alpha_v: " + Float.toString(alpha_v));
    	System.out.println("beta_v: " + Float.toString(beta_v));
    	System.out.println("gamma: " + Float.toString(gamma));
    	System.out.println("threshold_c: " + Integer.toString(threshold_c));
    	System.out.println("threshold_i: " + Float.toString(threshold_i));
		System.out.println("threshold_u: " + Float.toString(threshold_u));
    	System.out.println("threshold_c2: " + Float.toString(threshold_c2));
    	System.out.println("n: " + Integer.toString(n));
    	System.out.println("m: " + Integer.toString(m));
    	System.out.println("num_iterations: " + Integer.toString(num_iterations));
    	System.out.println("topK: " + Integer.toString(topK));
    	
    	System.out.println("（2）数据集选择：");
        // -------------------------- 判断实验的数据集 ------------------------
		if(fnTrainData == "/home/java/code/Java/MBPR/data/30music/30music-5k5k-train"){
            filename = "30music";   
			fnSimilarity_i = "/home/java/code/Java/MSBPR/similarity_matrixs/30music/similarity_i.txt";   
			fnSimilarity_u = "/home/java/code/Java/MSBPR/similarity_matrixs/30music/similarity_u.txt";
			fnSimilarity_i_u = "/home/java/code/Java/MSBPR/similarity_matrixs/30music/similarity_i_u.txt";
        }
        if(fnTrainData == "/home/java/code/Java/MBPR/data/rsc15/rsc15-5k5k-train"){
            filename = "rsc15";
			fnSimilarity_i = "/home/java/code/Java/MSBPR/similarity_matrixs/rsc15/similarity_i.txt";
			fnSimilarity_u = "/home/java/code/Java/MSBPR/similarity_matrixs/rsc15/similarity_u.txt";
			fnSimilarity_i_u = "/home/java/code/Java/MSBPR/similarity_matrixs/rsc15/similarity_i_u.txt";
        }
        if(fnTrainData == "/home/java/code/Java/MBPR/data/Tmall/tmall-5k5k-train"){
            filename = "tmall";
			fnSimilarity_i = "/home/java/code/Java/MSBPR/similarity_matrixs/Tmall/similarity_i.txt";
			fnSimilarity_u = "/home/java/code/Java/MSBPR/similarity_matrixs/Tmall/similarity_u.txt";
			fnSimilarity_i_u = "/home/java/code/Java/MSBPR/similarity_matrixs/Tmall/similarity_i_u.txt";
        }
		if(fnTrainData == "/home/java/code/Java/MBPR/data/aotm/aotm-5k5k-train"){   
            filename = "aotm";
			fnSimilarity_i = "/home/java/code/Java/MSBPR/similarity_matrixs/aotm/similarity_i.txt";
			fnSimilarity_u = "/home/java/code/Java/MSBPR/similarity_matrixs/aotm/similarity_u.txt";
			fnSimilarity_i_u = "/home/java/code/Java/MSBPR/similarity_matrixs/aotm/similarity_i_u.txt";
        }
		if(fnTrainData == "/home/java/code/Java/MBPR/data/yelp/yelp-5k5k-train"){   
            filename = "yelp";
			fnSimilarity_i = "/home/java/code/Java/MSBPR/similarity_matrixs/yelp/similarity_i.txt";
			fnSimilarity_u = "/home/java/code/Java/MSBPR/similarity_matrixs/yelp/similarity_u.txt";
			fnSimilarity_i_u = "/home/java/code/Java/MSBPR/similarity_matrixs/yelp/similarity_i_u.txt";
        }
		if(fnTrainData == "/home/java/code/Java/MBPR/data/last.fm/last.fm-5k5k-train"){   
            filename = "last.fm";
			fnSimilarity_i = "/home/java/code/Java/MSBPR/similarity_matrixs/last.fm/similarity_i.txt";
			fnSimilarity_u = "/home/java/code/Java/MSBPR/similarity_matrixs/last.fm/similarity_u.txt";
			fnSimilarity_i_u = "/home/java/code/Java/MSBPR/similarity_matrixs/last.fm/similarity_i_u.txt";
        }
        System.out.println("      数据集： " + filename);
    	System.out.println("fnTestData: " + fnTestData);
    	System.out.println("fnValidData: " + fnValidData);
    	System.out.println("fnValidData: " + fnValidData);

    	System.out.println("（3）相似度计算选择：");   	
    	System.out.println("flagsimilarity_i: " + Boolean.toString(flagsimilarity_i));
    	System.out.println("java: " + Boolean.toString(flagsimilarity_u));
    	System.out.println("flagsimilarity_i_u: " + Boolean.toString(flagsimilarity_i_u));
    	System.out.println("fnSimilarity_i: " + fnSimilarity_i);
    	System.out.println("fnSimilarity_u: " + fnSimilarity_u);
    	System.out.println("fnSimilarity_i_u: " + fnSimilarity_i_u);
    	
    	System.out.println("（4）划分方法选择："); 
    	System.out.println("flagsplit_i: " + Boolean.toString(flagsplit_i));
    	System.out.println("flagsplit_u: " + Boolean.toString(flagsplit_u));
		System.out.println("flagsplit_i_u: " + Boolean.toString(flagsplit_i_u));
        System.out.println("flagsplit_user: " + Boolean.toString(flagsplit_user));
    	
    	System.out.println("（5）评价指标选择：");
    	System.out.println("flagMRR: " + Boolean.toString(flagMRR));
    	System.out.println("flagMAP: " + Boolean.toString(flagMAP));
    	System.out.println("flagARP: " + Boolean.toString(flagARP));
    	System.out.println("flagAUC: " + Boolean.toString(flagAUC));    	
    	// ------------------------------
    	// --------------------------------------------------------------
    	// --- 因为n,m是在读完文件之后才计算出来的，所以放后面，但在Initialization之前
        itemRatingNumTrain = new int[m + 1]; // start from index "1"
        userRatingNumTrain = new int[n + 1]; // 

		// === Locate memory for the data structure of the model parameters
//        U = new float[n+1][d];
//        V = new float[m+1][d];
        biasV = new float[m+1];  // bias of item
        biasU = new float[n+1];  // bias of user
    	// --------------------------------------------------------------

    	// ------------------------------
        // float[] arr_i = {0.5f,0.6f,0.7f,0.8f,0.9f,0.92f,0.94f,0.96f,0.98f};
		// float[] arr_i = {0.9f,0.92f,0.94f,0.96f,0.98f};
      	// float[] arr_u = {0.5f,0.6f,0.7f,0.8f,0.9f};
		float[] arr_i = {0.01f,0.02f,0.04f,0.06f,0.08f,0.1f,0.12f};
		float[] arr_u = {0.02f,0.04f,0.06f,0.08f,0.1f, 0.12f};
      	float[] arr_c = {0.2f,0.3f,0.4f,0.5f,0.6f,0.7f,0.8f,0.9f};
      	int[] arr_p={1,2,3,4,5,6,7,8,9,10,15,20,25,30,35,40,45,50};
		int[] arr_d = {10, 50, 100, 150, 200};
      	int[] D = {20};
		int[] arr_topk = {20, 40, 60, 80, 100};
        float[] arr_lambda = {0f, 0.2f, 0.4f, 0.5f, 0.6f, 0.8f, 1f};
        float[] arr_lambda1 = {1f};
        for(float i : arr_i)
        {	
			fnresult_evaluation = "result_evaluation/aotm/msbpr_i/";
        	String st = String.valueOf(i);
			fnresult_evaluation = fnresult_evaluation +"arr_i"+ "(" + st + ")";
			System.out.println("fnresult_evaluation= " + fnresult_evaluation);
    
			flagvalid = false;  
			flagtest = true;
          	// -------------------------------------------------------
			num_iterations = 80000;
        	topK = 5;
        	d = 20;
            U = new float[n+1][d];
            V = new float[m+1][d];
            // -------------------------------------------------------
			// 每次计算之后清零
			Pre_ave = 0;
			Rec_ave = 0;
			F1_ave = 0;
			NDCG_ave = 0;
			Onecall_ave = 0;
			MRR_ave = 0;
			MAP_ave = 0;
			ARP_ave = 0;
			AUC_ave = 0;
			System.out.println("--------------------------------------------------------------" );

			alpha_u = alpha_v = beta_v = 0.01f;
			gamma = 0.01f;
            lambda = 0.5f;
			
            // msbpr 的相应参数设置
            flagsplit_i = true;
            flagsplit_u = false;
			flagsplit_i_u = false;
			threshold_c = 2;
			threshold_i = i;
			threshold_u = 0.04f;
			threshold_c2 = 0.4f;

            // msbpr_u 的相应参数设置
            flagsplit_user = false;
            threshold_user = 0.7f;
			
			System.out.println("d= " + d);
			System.out.println("topk= " + topK);
            System.out.println("lambda= " + lambda);
			System.out.println("flagsplit_i= " + flagsplit_i);
			System.out.println("flagsplit_u= " + flagsplit_u);
			System.out.println("flagsplit_i_u= " + flagsplit_i_u);
			System.out.println("threshold_c= " + threshold_c);
			System.out.println("threshold_i= " + threshold_i);
			System.out.println("threshold_u= " + threshold_u);
			System.out.println("threshold_c2= " + threshold_c2);
            System.out.println("threshold_user= " + threshold_user);
			
	        // === Step 1: Read data
	    	long TIME_START_READ_DATA = System.currentTimeMillis();
	    	readDataTrainTestValid();
	    	long TIME_FINISH_READ_DATA = System.currentTimeMillis();
	    	System.out.println("读取文件结束   Time (read data):" + 
	    				Float.toString((TIME_FINISH_READ_DATA-TIME_START_READ_DATA)/1000F)
	    				+ "s");
	    	// ------------------------------   	
	  
	    	// ------------------------------
	    	// === Step 2: Initialization of U, V, bias
	    	long TIME_START_INITIALIZATION = System.currentTimeMillis();
	    	initialize();
	    	long TIME_FINISH_INITIALIZATION = System.currentTimeMillis();
	    	System.out.println("参数初始化结束   Time (initialization):" + 
	    				Float.toString((TIME_FINISH_INITIALIZATION-TIME_START_INITIALIZATION)/1000F)
	    				+ "s");
	    	// ------------------------------
	
	        // === Step 3: Similarity
	       long TIME_START_SIM = System.currentTimeMillis();
	       similarity_i();
	       similarity_u();
	   	   similarity_i_u();
	       long TIME_FINISH_SIM = System.currentTimeMillis();
	       System.out.println("计算用户相似度结束， Time (similarity):" +
	               Float.toString((TIME_FINISH_SIM-TIME_START_SIM)/1000F)
	               + "s");
	        // ------------------------------
	    	
			// ------------------------------
			// === Step 4: Spliting		
			long TIME_START_SPLIT = System.currentTimeMillis();
			split_i();
			split_u();
			split_i_u();
            split_user();
			long TIME_FINISH_SPLIT = System.currentTimeMillis();
			System.out.println("划分数据集结束， Time (spliting):" +
					Float.toString((TIME_FINISH_SPLIT-TIME_START_SPLIT)/1000F)
					+ "s");
	       // ------------------------------
			
			// ------------------------------
	
	//    	for (int iter = 1; iter <= num_iterations; iter++)
	//    	{
	//    		// === Step 5: Training
	//    		train();
	//			train_1();
	//			if (iter % 10000 == 0)
	//			{
	//				System.out.println("---------第  " + iter + " 次迭代结束");
	//			}
			int nnn = 1;
			for(int num =1; num<=nnn; num++)
			{
				System.out.println("第" + num + "次");
				// === Step 5: Training
				long TIME_START_TRAIN = System.currentTimeMillis();
				train_i();   // msbpr 对应的train
                // train_u();   // msbpr_u 对应的train
				// train_i_u();   // bpr + bpr_u
                // train_i_ulv();  // bpr + msbpr_u 
				// train_ipcj_u();  // msbpr + bpr_u
				// train_ipcj_ulv(); // msbpr + msbpr_u
				long TIME_FINISH_TRAIN = System.currentTimeMillis();
				System.out.println(num_iterations + "次迭代结束" + "  Time(training):" +
						Float.toString((TIME_FINISH_TRAIN - TIME_START_TRAIN) / 1000F) + "s");
				//        	System.out.println("---------第  " + iter + " 次迭代结束" + "  Time (training):" +
				//    				Float.toString((TIME_FINISH_TRAIN-TIME_START_TRAIN)/1000F)
				//    				+ "s");
	
	        	// ------------------------------
	
	        	// ------------------------------
	        	// === Step 6: Prediction and Evaluation
	        	if (flagvalid)
	        	{	
	        		System.out.println("--- valid ---");
	        		long TIME_START_VALID = System.currentTimeMillis();
	    	    	testRanking(ValidData);
	    	    	long TIME_FINISH_VALID = System.currentTimeMillis();
	    	    	System.out.println("Elapsed Time (validation):" +
	    	    				Float.toString((TIME_FINISH_VALID-TIME_START_VALID)/1000F)
	    	    				+ "s");
	        	}
				// === Step 7: TestRanking
	        	if (flagtest)
	        	{
	        		System.out.println("--- test ---");
	        		long TIME_START_TEST = System.currentTimeMillis();
	        		testRanking(TestData);
	        		long TIME_FINISH_TEST = System.currentTimeMillis();
	        		System.out.println("测试结束    Time (test):" +
	        					Float.toString((TIME_FINISH_TEST-TIME_START_TEST)/1000F)
	        					+ "s");
	        	}
	        	 // ------------------------------
			}
			// ---------- 取多次测量的平均值 --------------
//			 System.out.println("多次测量后的平均值："); 
//			 System.out.println("Prec_ave@"+Integer.toString(5)+":"+Float.toString(Pre_ave/nnn));
//			 System.out.println("Rec_ave@"+Integer.toString(5)+":"+Float.toString(Rec_ave/nnn));  
//			 System.out.println("F1_ave@"+Integer.toString(5)+":"+Float.toString(F1_ave/nnn)); 
//			 System.out.println("NDCG_ave@"+Integer.toString(5)+":"+Float.toString(NDCG_ave/nnn)); 
////			 System.out.println("1-call_ave@"+Integer.toString(5)+":"+Float.toString(Onecall_ave/nnn)); 
//			 System.out.println("MRR_ave@"+Integer.toString(5)+":"+Float.toString(MRR_ave/nnn)); 
//			 System.out.println("MAP_ave@"+Integer.toString(5)+":"+Float.toString(MAP_ave/nnn)); 
////			 System.out.println("ARP_ave@"+Integer.toString(5)+":"+Float.toString(ARP_ave/nnn)); 
//			 System.out.println("AUC_ave@"+Integer.toString(5)+":"+Float.toString(AUC_ave/nnn)); 

//			System.out.println("--------------------"); 
//			System.out.println(Float.toString(Pre_ave/nnn));		
//			System.out.println(Float.toString(Rec_ave/nnn));  
//			System.out.println(Float.toString(F1_ave/nnn)); 
//			System.out.println(Float.toString(NDCG_ave/nnn)); 
//			System.out.println(Float.toString(MRR_ave/nnn)); 
//			System.out.println(Float.toString(MAP_ave/nnn)); 

        }
//    	}
    	

    }
          
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void readDataTrainTestValid() throws Exception
    {	
    	// ---------------------TrainData------------------------
    	BufferedReader br = new BufferedReader(new FileReader(fnTrainData));
    	String line = null;    			
    	while ((line = br.readLine())!=null)
    	{
    		String[] terms = line.split("\t");
    		int userID = Integer.parseInt(terms[0]);    		
    		int itemID = Integer.parseInt(terms[1]);
    		int count = Integer.parseInt(terms[2]);
    		
    		// --- add to the whole item set
    		ItemSetWhole.add(itemID);
		    			
    		// --- add to the whole user set
    		UserSetWhole.add(userID);
    		
		    // --- statistics, used to calculate the interacitions of each item for all users
		    itemRatingNumTrain[itemID] += count;  // 引入了交互次数，所以物品偏好也得把次数加上
    		   		
			// TrainData: user->items   (找该用户交互过的所有物品集及次数)
			if(TrainData.containsKey(userID))
			{
				HashMap<Integer, Integer> itemset = TrainData.get(userID);    	
	    		itemset.put(itemID, count);
	    		TrainData.put(userID, itemset);
	    	}
	    	else
	    	{
				HashMap<Integer, Integer> itemset = new HashMap<Integer, Integer>();
				itemset.put(itemID, count);
				TrainData.put(userID, itemset);
	    	}

			// Data: user->items   (找该用户交互过的所有物品集及次数)
			if(Data.containsKey(userID))
			{
				HashMap<Integer, Integer> itemset = Data.get(userID);
				itemset.put(itemID, count);
				Data.put(userID, itemset);
			}
			else
			{
				HashMap<Integer, Integer> itemset = new HashMap<Integer, Integer>();
				itemset.put(itemID, count);
				Data.put(userID, itemset);
			}

			// TrainDataItem2User: item->users   （找交互过该物品的所有用户集及次数）
			if(TrainDataItem2User.containsKey(itemID))
    	    {
    	    	HashMap<Integer, Integer> userSet = TrainDataItem2User.get(itemID);
    	    	if (userSet.size()<10000)
    	    	{
        	   		userSet.put(userID, count);
        	   		TrainDataItem2User.put(itemID, userSet);
    	    	}
    	    }
    	    else
    	    {
    	    	HashMap<Integer, Integer> userSet = new HashMap<Integer, Integer>();
    	    	userSet.put(userID, count);
    	    	TrainDataItem2User.put(itemID, userSet);
    	    }
			// DataItem2User: item->users   （找交互过该物品的所有用户集及次数）
			if(DataItem2User.containsKey(itemID))
    	    {
    	    	HashMap<Integer, Integer> userSet = DataItem2User.get(itemID);
    	    	if (userSet.size()<10000)
    	    	{
        	   		userSet.put(userID, count);
        	   		DataItem2User.put(itemID, userSet);
    	    	}
    	    }
    	    else
    	    {
    	    	HashMap<Integer, Integer> userSet = new HashMap<Integer, Integer>();
    	    	userSet.put(userID, count);
    	    	DataItem2User.put(itemID, userSet);
    	    }
    	}
    	br.close();
    	// ======================================================
		// ==================== 生成Itemdislike ===================
		for(Integer i:TrainDataItem2User.keySet())
		{
			if(TrainDataItem2User.get(i).size() <= threshold_c)
			{
				ItemSetDislike.add(i);
			}
		}
		System.out.println("size_itemdislike: " + ItemSetDislike.size());
		// ------------------------------------------------------

    	// ---------------------TestData-------------------------
    	if (fnTestData.length()>0) {
			br = new BufferedReader(new FileReader(fnTestData));
			String line1 = null;
			while ((line1 = br.readLine()) != null) {
				String[] terms = line1.split("\t");
				int userID = Integer.parseInt(terms[0]);
				int itemID = Integer.parseInt(terms[1]);
				int count = Integer.parseInt(terms[2]);

				// --- add to the whole item set
				ItemSetWhole.add(itemID);

				// --- add to the whole user set
				UserSetWhole.add(userID);

				// --- test data
				if (TestData.containsKey(userID)) {
					HashSet<Integer> itemSet = TestData.get(userID);
					itemSet.add(itemID);
					TestData.put(userID, itemSet);
				} else {
					HashSet<Integer> itemSet = new HashSet<Integer>();
					itemSet.add(itemID);
					TestData.put(userID, itemSet);
				}

				// Data: user->items   (找该用户交互过的所有物品集及次数)
				if(Data.containsKey(userID))
				{
					HashMap<Integer, Integer> itemset = Data.get(userID);
					itemset.put(itemID, count);
					Data.put(userID, itemset);
				}
				else
				{
					HashMap<Integer, Integer> itemset = new HashMap<Integer, Integer>();
					itemset.put(itemID, count);
					Data.put(userID, itemset);
				}
				// DataItem2User: item->users   （找交互过该物品的所有用户集及次数）
				if(DataItem2User.containsKey(itemID))
	    	    {
	    	    	HashMap<Integer, Integer> userSet = DataItem2User.get(itemID);
	    	    	if (userSet.size()<10000)
	    	    	{
	        	   		userSet.put(userID, count);
	        	   		DataItem2User.put(itemID, userSet);
	    	    	}
	    	    }
	    	    else
	    	    {
	    	    	HashMap<Integer, Integer> userSet = new HashMap<Integer, Integer>();
	    	    	userSet.put(userID, count);
	    	    	DataItem2User.put(itemID, userSet);
	    	    }
			}
			br.close();
		}
		// =======================================================
   	
    	// ----------------------ValidData------------------------
    	if (fnValidData.length()>0)
    	{
	    	br = new BufferedReader(new FileReader(fnValidData));
	    	line = null;
	    	while ((line = br.readLine())!=null)
	    	{
	    		String[] terms = line.split("\t");
	    		int userID = Integer.parseInt(terms[0]);
	    		int itemID = Integer.parseInt(terms[1]);  
	    		int count = Integer.parseInt(terms[2]);
	    	
	    		// --- add to the whole item set
				ItemSetWhole.add(itemID);

				// --- add to the whole user set
				UserSetWhole.add(userID);
				
				// --- validation data
				if(ValidData.containsKey(userID))
	    		{
	    			HashSet<Integer> itemSet = ValidData.get(userID);
	    			itemSet.add(itemID);
	    			ValidData.put(userID, itemSet);
	    		}
	    		else
	    		{
	    			HashSet<Integer> itemSet = new HashSet<Integer>();
	    			itemSet.add(itemID);
	    			ValidData.put(userID, itemSet);
	    		}
				// Data: user->items   (找该用户交互过的所有物品集及次数)
				if(Data.containsKey(userID))
				{
					HashMap<Integer, Integer> itemset = Data.get(userID);
					itemset.put(itemID, count);
					Data.put(userID, itemset);
				}
				else
				{
					HashMap<Integer, Integer> itemset = new HashMap<Integer, Integer>();
					itemset.put(itemID, count);
					Data.put(userID, itemset);
				}
				// DataItem2User: item->users   （找交互过该物品的所有用户集及次数）
				if(DataItem2User.containsKey(itemID))
	    	    {
	    	    	HashMap<Integer, Integer> userSet = DataItem2User.get(itemID);
	    	    	if (userSet.size()<10000)
	    	    	{
	        	   		userSet.put(userID, count);
	        	   		DataItem2User.put(itemID, userSet);
	    	    	}
	    	    }
	    	    else
	    	    {
	    	    	HashMap<Integer, Integer> userSet = new HashMap<Integer, Integer>();
	    	    	userSet.put(userID, count);
	    	    	DataItem2User.put(itemID, userSet);
	    	    }

	    	}
	    	br.close();
    	}
    	// ----------------------------------------------------
    	
    }
    


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void initialize()
    {
    	// ======================================================
    	Random r = new Random(1);
    	// --- initialization of U and V
    	for (int u=1; u<n+1; u++)
    	{
    		for (int f=0; f<d; f++)
    		{
    			U[u][f] = (float) ( Math.sqrt(0.01) * r.nextGaussian());
    		}
    	}
    	//
    	for (int i=1; i<m+1; i++)
    	{
    		for (int f=0; f<d; f++)
    		{
    			V[i][f] = (float) ( Math.sqrt(0.01) * r.nextGaussian());
    		}
    	}
    	// ======================================================

    	// ======================================================
    	// --- initialization of \mu and b_i
    	float g_avg = 0;
    	for (int i=1; i<m+1; i++)
    	{
    		g_avg += itemRatingNumTrain[i];
    	}
    	g_avg = g_avg/n/m;  // 整个数据集的平均打分
    	System.out.println( "The global average rating:" + Float.toString(g_avg) );

    	// --- biasV[i] represents the popularity of the item i, which is initialized to [0,1]
    	for (int i=1; i<m+1; i++)
    	{
    		 biasV[i]= (float) itemRatingNumTrain[i] / n - g_avg;
    	}
        // === biasU[n] represents the popularity of the user u, which is initialized to [0,1]
        for (int u=1; u<n+1; u++)
        {
            biasU[u]= (float) userRatingNumTrain[u] / m - g_avg;
        }
    	// $ \mu = \sum_{u,i} y_{ui} /n/m $
    	// $ b_i = \sum_{u=1}^n (y_{ui} - \mu) / n $
    	// ======================================================
    }
	// ===================== 相似度计算 =========================
    public static void similarity_i() {
    	/**
    	 * 这里提前计算好物品之间的相似度矩阵并储存在文件中，节约时间，方便后面调用和调参等。
		 * jaccard
    	 * */
    	if(flagsimilarity_i) 
    	{	
	    	System.out.println("similarity:  similarity_item");  
	    	double [][] S = new double [m+1][m+1];
	     	double fenzi = 0;
	    	Integer fenmu = 0;
	    	List<Integer> list1 = new ArrayList<Integer>(); // 定义list1储存筛选出来的i1物品列向量
	    	List<Integer> list2 = new ArrayList<Integer>(); // 定义list2储存筛选出来的i2物品列向量
	    	
	    	// ----------------- 计算所有物品之间的相似度 ------------------
	    	for(int i1 = 1; i1 <= m; i1++)
	    	{
	    		// === 交互过物品i1的用户集合
				if (!TrainDataItem2User.containsKey(i1)){    
					continue;
				}
	    		List<Integer> userset_i1 = new ArrayList<Integer>(TrainDataItem2User.get(i1).keySet()); 
                // System.out.println("list1: " + userset_i1.size() + ' ' + userset_i1);
	    		
	    		for(int i2 = i1+1; i2 <= m; i2++) 
	    		{

    				// --list清零
    				list1.clear();
    				list2.clear();
    				// fenzi,fenmu清零
	    			fenzi = fenmu = 0;
        			// === 交互过物品i2的用户集合
					if (!TrainDataItem2User.containsKey(i2)){    
						continue;
					}
        			List<Integer> userset_i2 = new ArrayList<Integer>(TrainDataItem2User.get(i2).keySet());
					// System.out.println("list2: " + userset_i2.size() + ' ' + userset_i2);
                    List<Integer> all_userset = new ArrayList<>(userset_i1);
                    all_userset.removeAll(userset_i2);
                    all_userset.addAll(userset_i2);
                    // System.out.println("all_userset: " + all_userset.size() + ' ' + all_userset);
                    fenmu = all_userset.size();
                    // System.out.println("fenmu: " + fenmu);
        			
        			// === 同时交互过物品i1和i2的用户   
        			for(Integer u:userset_i1) {
        				if(userset_i2.contains(u)) {
        					fenzi += Math.pow(Math.E, -1 * (Math.abs(TrainData.get(u).get(i1) - TrainData.get(u).get(i2))));
        				}
        			}
        			if (fenzi == 0)  // 如果分子=0, 则说明两个物品没有共同交互过的用户，则直接令相似度=0
        			{
        				S[i1][i2] = 0;
                        // System.out.println("没有交集");
        			}
        			else
        			{
        				S[i1][i2] = fenzi / fenmu;
                        // System.out.println("有交集!!!!");
        			}
        	
	   			System.out.println("S(i)" + i1 + "-" + i2 + ":  " + S[i1][i2]);
	    		}
    	    }
            // ---------------- 将相似度矩阵写入文件方便后面调用 ------------------
            try {
                
                System.out.println("正在写入文件(similarity_i)..........");
                
                // 创建两个输出流控制方式
                PrintStream out = System.out;

                PrintStream ps = new PrintStream(fnSimilarity_i);
                System.setOut(ps);
                
                for(int i=0; i<S.length; i++) 
                {
                    for(int j=0; j<S[0].length; j++) 
                    {
                        if(j==i) {
                            S[i][j]=1;
                            System.out.print(S[i][j] + "\t");
                        }
                        else if(j>i){
                            System.out.print(S[i][j] + "\t");
                        }
                        else if(j<i) {
                            S[i][j] = S[j][i];
                            System.out.print(S[i][j] + "\t");
                        }
                    }
                    System.out.println();
                }
                
                System.setOut(out);
                System.out.println("文件(similarity_i)写入完毕，请查看相应的文件。");
                
            }catch(FileNotFoundException e) {
                e.printStackTrace();
            }
    }

    }

	public static void similarity_u() {
    	
    	/**
    	 * 这里提前计算好用户之间的相似度并储存在文件中，节约时间，方便后面调用调参等。
		 * jaccard
    	 * */
    	if(flagsimilarity_u)
    	{
    	System.out.println("similarity:  similarity_user");
    	double [][] S = new double [n+1][n+1];
    	double fenzi = 0;
    	double fenmu = 0;
    	List<Integer> list1 = new ArrayList<Integer>(); // 定义list1储存筛选出来的i1物品列向量
        List<Integer> list2 = new ArrayList<Integer>(); // 定义list2储存筛选出来的i2物品列向量

    	// ----------------- 计算所有用户之间的相似度 -----------------

    	for (int u1 = 1; u1 <= n; u1++)
    	{
    		if (!TrainData.containsKey(u1)){    
				continue;
			}
    		List<Integer> Iu1_i = new ArrayList<Integer>(TrainData.get(u1).keySet());        // === Iu1_i
			// System.out.println("Iu1: " + Iu1_i.size() + " " + Iu1_i);

    		for (int u2 = u1+1; u2 <= n; u2++)
    		{
    			// list 清零
    			list1.clear();
    			list2.clear();
    			// fenzi,fenmu1,fenmu2清零
    			fenzi = fenmu = 0;
    			if (!TrainData.containsKey(u2)){    
					continue;
				}
    			List<Integer> Iu2_i = new ArrayList<Integer>(TrainData.get(u2).keySet());   // === Iu2_i
				// System.out.println("Iu2: " + Iu2_i.size() + " " + Iu2_i);
				List<Integer> all_itemset = new ArrayList<>(Iu1_i);
				all_itemset.removeAll(Iu2_i);
				all_itemset.addAll(Iu2_i);
				// System.out.println("all_itemset: " + all_itemset.size() + ' ' + all_itemset);
				fenmu = all_itemset.size();

    			for(Integer i:Iu1_i)
    			{
    				if(Iu2_i.contains(i))
    				{
    					fenzi += Math.pow(Math.E, -1 * Math.abs(TrainData.get(u1).get(i) - TrainData.get(u2).get(i)));
    				}
    			}
    			if (fenzi == 0)  // 如果分子=0则说明两个用户没有共同交互过的物品，则直接令相似度=0
    			{
    				S[u1][u2] = 0;
					// System.out.println("没有交集");
    			}
    			else            // 计算cosine相似度
    			{
    				S[u1][u2] = fenzi / fenmu;
					// System.out.println("有交集!!!!");
    			}

    			System.out.println("S(u)" + u1 + "-" + u2 + ": " + S[u1][u2]);

    		}
    	}
    	
    	// -------------- 将相似度矩阵写入文件储存方便后面调用 ---------------
    	try {
    		
			System.out.println("正在写入文件(similarity_u)..........");
			
			// 创建两个输出流控制方式
			PrintStream out = System.out;

			PrintStream ps = new PrintStream(fnSimilarity_u);
			System.setOut(ps);

			
			for(int i=0; i<S.length; i++) 
			{
				for(int j=0; j<S[0].length; j++) 
				{
					if(j==i) {
						S[i][j]=0;
						System.out.print(S[i][j] + "\t");
					}
					else if(j>i){
						System.out.print(S[i][j] + "\t");
					}
					else if(j<i) {
						S[i][j] = S[j][i];
						System.out.print(S[i][j] + "\t");
					}
				}
				System.out.println();
			}
			
			System.setOut(out);
			System.out.println("文件(similarity_u)写入完毕，请查看相应的文件。");
    		
    	}catch(FileNotFoundException e) {
    		e.printStackTrace();
    	}
      }
    }
    
	public static void similarity_i_u() throws Exception{

        /* *
         ** 这里提出一个新的cosine相似度计算方法，相比传统的cosine计算方法条件放松了
         ** 更适用于冷启动数据
         * */
    	if(flagsimilarity_i_u) 
    	{

        double [][] S_i = new double [m+1][m+1]; // 物品相似度
        double [][] S_u = new double [n+1][n+1]; // 用户相似度
        double a, b ;  // 用于储存新cosine计算公式中的分母部分计算结果
        double fenzi = 0;
        int n_ = 0;
//        int item1 =0; // 用于临时储存对item1的交互次数
//        int item2 =0; // 用于临时储存对item2的交互次数

        // ------- 读取物品相似度文件,把物品相似度矩阵数据读取出来调用 ----------
        BufferedReader br = new BufferedReader(new FileReader(fnSimilarity_i));
        String line = null;
        while((line = br.readLine())!= null) {

            String[] terms = line.split("\t");

            for(int i=0; i<terms.length; i++) {

                S_i[n_][i] = Double.parseDouble(terms[i]);
            }

            n_++;
        }

        System.out.println("相似度矩阵S大小：" + "行: " + S_i.length + " 列: " + S_i[0].length);

        // ------------------------- 用新cosine公式计算用户相似度 -------------------------
//        long timetime1 = 1;
//        long cntcnt = 0;
        for(int u1 = 1; u1 <= n; u1++){
			if (!TrainData.containsKey(u1)){    
				continue;
			}
            List<Integer> Iu1_items = new ArrayList<Integer>(TrainData.get(u1).keySet());
            List<Integer> Iu1_items_values = new ArrayList<Integer>(TrainData.get(u1).values());
            int num_u1_items = Iu1_items.size();
//            System.out.println(Iu1_items_values);
            a = sum(Iu1_items_values);  // 用户u1交互过的物品列向量的平方和
            
            for(int u2 = u1 + 1; u2 <= n; u2++){
            	
            	fenzi = 0;
//                if(u2 == u1){
//                    S_u[u1][u2] = 0;
//                }
//                else{
					if (!TrainData.containsKey(u2)){    
						continue;
					}
                    List<Integer> Iu2_items = new ArrayList<Integer>(TrainData.get(u2).keySet());
                    List<Integer> Iu2_items_values = new ArrayList<Integer>(TrainData.get(u2).values());
                    int num_u2_items = Iu2_items.size();
                    b = sum(Iu2_items_values);  // 用户u2交互过的物品列向量的平方和
                    S_u[u1][u2] = (Math.sqrt(a*num_u2_items)) * (Math.sqrt(b*num_u1_items));  // 新cosine计算公式的分母部分计算结果

                    for (Integer i1:Iu1_items){
                    	                
                        for (Integer i2:Iu2_items){

                            fenzi += TrainData.get(u1).get(i1) * TrainData.get(u2).get(i2) * S_i[i1][i2];  // 新cosine计算公式的分子部分计算结果
                            
                        }
                    }

                    S_u[u1][u2] = fenzi / S_u[u1][u2];
//                }
                                
                System.out.println("S(i_u)" + u1 + "-" + u2 + ": " + S_u[u1][u2]);
                
            }
        }

    	// -------------- 将相似度矩阵写入文件储存方便后面调用 ---------------
    	try {
    		
			System.out.println("正在写入文件(similarity_i_u)..........");
			
			// 创建两个输出流控制方式
			PrintStream out = System.out;
			PrintStream ps = new PrintStream(fnSimilarity_i_u);
			System.setOut(ps);
			
			for(int i=0; i<S_u.length; i++) 
			{
				for(int j=0; j<S_u[0].length; j++) 
				{
//					System.out.print(S_u[i][j] + "\t");
					if(j==i) {
						S_u[i][j]=0;
						System.out.print(S_u[i][j] + "\t");
					}
					else if(j>i){
						System.out.print(S_u[i][j] + "\t");
					}
					else if(j<i) {
						S_u[i][j] = S_u[j][i];
						System.out.print(S_u[i][j] + "\t");
					}
				}
				System.out.println();
			}
			
			System.setOut(out);
			System.out.println("文件(similarity_i_u)写入完毕，请查看相应的文件。");
    		
    	}catch(FileNotFoundException e) {
    		e.printStackTrace();
    	}
      }
    }
    
    // ===================== 横向物品划分 =======================
    public static void split_i() throws Exception
    {
    	if(flagsplit_i) 
    	{

    	System.out.println("split:  split_i");
    	double [][] S = new double [m+1][m+1];
    	int n_ = 0;

    	BufferedReader br = new BufferedReader(new FileReader(fnSimilarity_i));
    	String line = null;    			
    	while((line = br.readLine())!= null) {
    		
    		String[] terms = line.split("\t");
    		
    		for(int i=0; i<terms.length; i++) {
    			
    			S[n_][i] = Double.parseDouble(terms[i]);
    		}
    		
    		n_++;
    	}   	
    	
    	System.out.println("相似度矩阵S大小：" + "行: " + S.length + " 列: " + S[0].length);	
    	
    	// -------------------- 根据物品相似度划分数据集 --------------------------
		int num_small_p = 0;
		int num_mid_p = 0;
		int num_big_p = 0;
        for (int u = 1; u <= n; u++)
        {
            //System.out.println("开始划分数据集的用户：" + u);

            // ------------------------Iu_i---------------------------
            List<Integer> Iu_i = new ArrayList<Integer>(TrainData.get(u).keySet());    // === Iu_i
            int Size_Iu_i = Iu_i.size();                                               // 用户u交互物品集数量
            List<Integer> Iu_i_count = new ArrayList<Integer>(TrainData.get(u).values()); // 取出用户u交互过的物品集相应的交互次数
            //System.out.println("Iu_i_count:" + Iu_i_count);
            int sum = 0;
            for(Integer c:Iu_i_count) {
                sum += c;                                                              // 计算用户u交互物品次数总和,后面对计算权重有用
            }
            //System.out.println("sum: " + sum);

            // ------------------------Iu_j---------------------------
            List<Integer> Iu_j = new ArrayList<Integer>(ItemSetDislike);               // 取出大家都不喜欢的物品集Idislike
            //System.out.println("Itemsetdislike: " + ItemSetDislike.size() + "个 " + ItemSetDislike);
            Iu_j.removeAll(Iu_i);                                                      // === Iu_j
            Iu_j_data.put(u, Iu_j);
            int Size_Iu_j = Iu_j.size();

            // ----------------------Iu_*(Iu_unknow)------------------
            List<Integer> Iu_unknow = new ArrayList<Integer>(ItemSetWhole);            // 取出train中的所有物品集
            //System.out.println("Itemsetwhole: " + ItemSetWhole.size() + "个");
            Iu_unknow.removeAll(Iu_i);
            Iu_unknow.removeAll(Iu_j);                                                 // === Iu_unknow(Iu_*)
            int Size_Iu_unknow = Iu_unknow.size();                                     // Iu_*的大小
            //System.out.println("Iu_*: " + Size_Iu_unknow + "个  " + Iu_unknow);

            // -------------------------similarity--------------------
            // --- caculate the similarity_i with weight
			HashMap<Integer, Double> similarity = new HashMap<Integer, Double>();  // 用于储存Iu_*物品ID及相应的加权相似度
			similarity.clear();
			
            for(Integer i1:Iu_i)
            {
                double weight = TrainData.get(u).get(i1);
                weight = weight/sum;  // 计算对于i1来说的cosine相似度的权重
//                System.out.println("weight: " + weight);

                for(Integer i2:Iu_unknow)
                {
                	// === 计算带权相似度
                	double cosine = S[i1][i2] * weight;
//	        		System.out.println(cosine);

                    // --- 根据i2的物品ID值来储存其相似度值，并按权求和
                    if(similarity.containsKey(i2))
                    {
                        Double cos = similarity.get(i2);
                        cos += cosine;
                        similarity.put(i2, cos);
                    }
                    else
                    {
                        similarity.put(i2, cosine);
                    }

                }
            }
            //System.out.println("similarity: " + similarity.size() + "个 " + similarity.values());

            // -----------------------sort----------------------
            // --- (妙啊，带着ID对预测评分排序)
            List<Map.Entry<Integer,Double>> sorted_similarity =
                    new ArrayList<Map.Entry<Integer,Double>>(similarity.entrySet());
            Collections.sort(sorted_similarity, new Comparator<Map.Entry<Integer,Double>>()
            {
                public int compare( Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2 )
                {
                    return o2.getValue().compareTo( o1.getValue() ); // 降序排序
                }
            });
	    		//System.out.print("排序后: ");
	    		//for(Entry<Integer, Double> i3:sorted_similarity) {
	    		//	System.out.print(i3.getValue() + " ,  ");
	    		//}
	    		//System.out.println("\n");

            // -----------------------Iu_p---------------------------
            List<Integer> Iu_p = new ArrayList<Integer>();
            for(Entry<Integer, Double> i4:sorted_similarity) {
                if(i4.getValue() >= threshold_i)
                {
                    Iu_p.add(i4.getKey());                                   // === Iu_p
                }
            }
            Iu_p_data.put(u, Iu_p);

            // -----------------------Iu_uk--------------------------
            List<Integer> Iu_uk = new ArrayList<Integer>(Iu_unknow);
            Iu_uk.removeAll(Iu_p);                                           // === Iu_uk
            Iu_uk_data.put(u, Iu_uk);
            
            // ----- 输出测试 -----
//            System.out.println("Iu_i:" + Size_Iu_i + "个 " + Iu_i);
        //    System.out.println("Iu_p: " + Iu_p.size() + "个 ");
//            System.out.println("Iu_uk: " + Iu_uk.size() + "个");
//            System.out.println("Iu_j: " + Size_Iu_j + "个 " + Iu_j);
			if (Iu_p.size() < 5){
				num_small_p += 1;
			}
			else if (5 <= Iu_p.size() && Iu_p.size() < 10){
				num_mid_p += 1;
			}
			else{
				num_big_p += 1;
			}
        }
		// System.out.println("潜在物品数小于5的用户数：" + num_small_p);
		// System.out.println("潜在物品数5~10的用户数：" + num_mid_p);
		// System.out.println("潜在物品数大于10的用户数：" + num_big_p);
      }
    }
     	
	public static void split_u () throws Exception
    {
    	if(flagsplit_u) 
    	{
    	System.out.println("split:  split_u");

    	double [][] S = new double [n+1][n+1];
    	int n_ = 0;
    	
		BufferedReader br = new BufferedReader(new FileReader(fnSimilarity_u));
    	String line = null;    			
    	while((line = br.readLine())!= null) {
    		
    		String[] terms = line.split("\t");
    		
    		for(int i=0; i<terms.length; i++) {
    			
    			S[n_][i] = Double.parseDouble(terms[i]);
    		}
    		
    		n_++;
    	}br.close();   	
    	// System.out.println("矩阵第二行：" + Arrays.toString(S[1]));	
    	System.out.println("用户相似度矩阵S大小：" + "行: " + S.length + " 列: " + S[0].length);	
			   	
    	// -------------- 根据相似度预测交互次数并划分数据集 ----------
		HashMap<Integer, Double> u_s = new HashMap<Integer, Double>();  // 用于储存用户与其他所有用户对应的相似度值（每次循环完需要清除一下）
		List<Integer> similar_user = new ArrayList<Integer>();   // 用于储存每个用户对应的相似用户集合（每次循环完需要清除一下）

        for (int u = 1; u <= n; u++)
        {
			similar_user.clear();			
			u_s.clear();

			for(int u1 = 1; u1 <= n; u1++)
			{
				u_s.put(u1, S[u][u1]);   // 给对应的每个用户相似度带上用户序号，方便后面排序筛选
			}

			// ------------- 根据用户相似度排序，筛选出相似用户 ----------
			List<Map.Entry<Integer,Double>> sorted_user = new ArrayList<Map.Entry<Integer,Double>>(u_s.entrySet());
			Collections.sort(sorted_user, new Comparator<Map.Entry<Integer,Double>>()
			{
				public int compare( Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2 )
				{
					return o2.getValue().compareTo( o1.getValue() ); // 降序排序
				}
			});

		//    System.out.print("排序后: ");
        //    for(Entry<Integer, Double> i3:sorted_user) {
        //        System.out.print(i3.getValue() + " ,  ");
        //    }
        //    System.out.println("\n");

			int sum_num = 0;
            for(Entry<Integer, Double> i4:sorted_user) {
                if(i4.getValue() >= threshold_u)
                {
					sum_num += 1;
                    similar_user.add(i4.getKey());                                   
                }
            }
//			 System.out.println("相似用户集合大小：" + sum_num);
			// ------------------------------------------------------

            HashMap<Integer, Double> r_unknow_item = new HashMap<Integer, Double>();     // 用于储存填充后的unknown物品交互情况

            // ------------------------Iu_i---------------------------
            List<Integer> Iu_i = new ArrayList<Integer>(TrainData.get(u).keySet());    // === Iu_i
            int Size_Iu_i = Iu_i.size();                                               // 用户u交互物品集数量
//            System.out.println("Iu_i:" + Size_Iu_i + "个 " + Iu_i);
            List<Integer> Iu_i_count = new ArrayList<Integer>(TrainData.get(u).values()); // 取出用户u交互过的物品集相应的交互次数
//            System.out.println("Iu_i_count:" + Iu_i_count);
            int sum = 0;
            for(Integer c:Iu_i_count) {
                sum += c;                                                              // 计算用户u交互物品次数总和
            }

            // ------------------------Iu_j---------------------------
            List<Integer> Iu_j = new ArrayList<Integer>(ItemSetDislike);               // 取出大家都不喜欢的物品集Idislike
//            System.out.println("Itemsetdislike: " + ItemSetDislike.size() + "个 " + ItemSetDislike);
            Iu_j.removeAll(Iu_i);                                                      // === Iu_j
            Iu_j_data.put(u, Iu_j);
            int Size_Iu_j = Iu_j.size();
//            System.out.println("Iu_j: " + Size_Iu_j + "个 " + Iu_j);

            // ----------------------Iu_*(Iu_unknow)------------------
            List<Integer> Iu_unknow = new ArrayList<Integer>(ItemSetWhole);            // 取出train中的所有物品集
//            System.out.println("Itemsetwhole: " + ItemSetWhole.size() + "个");
            Iu_unknow.removeAll(Iu_i);
            Iu_unknow.removeAll(Iu_j);                                                 // === Iu_unknow(Iu_*)
            int Size_Iu_unknow = Iu_unknow.size();                                     // Iu_*的大小
//            System.out.println("Iu_*: " + Size_Iu_unknow + "个  " + Iu_unknow);

            // -------------------------预测交互次数--------------------
			for(Integer i:Iu_unknow)
			{
				double r=0, s_sum=0;
				for(Integer uu:similar_user)      // 遍历相似用户
				{
					s_sum += S[u][uu];            // 所有权重和(分母)
					if(Data.get(uu).get(i) == null)
					{
						r += 0;  // (分子)
					}
					else
					{
						r += S[u][uu] * Data.get(uu).get(i);  // 用户相似度sim(u,uu)作为权重，乘上用户uu对物品i的交互次数(分子)
					}
				}
				r = r/s_sum;
				// System.out.println(r);
            	r_unknow_item.put(i, r);
			}

            // -----------------------sort----------------------
            // --- (妙啊，带着ID对预测评分排序)
            List<Map.Entry<Integer,Double>> sorted_r_unknow_item =
                    new ArrayList<Map.Entry<Integer,Double>>(r_unknow_item.entrySet());
            Collections.sort(sorted_r_unknow_item, new Comparator<Map.Entry<Integer,Double>>()
            {
                public int compare( Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2 )
                {
                    return o2.getValue().compareTo( o1.getValue() ); // 降序排序
                }
            });
//            System.out.print("排序后: ");
//            int nnnnn =0;
//            for(Entry<Integer, Double> i3:sorted_r_unknow_item) {
//            	nnnnn +=1;
//            	if(nnnnn<=20) {
//                System.out.print(i3.getValue() + " ,  ");
//            	}
//            }
//            System.out.println("\n");

            // -----------------------Iu_p---------------------------
            List<Integer> Iu_p = new ArrayList<Integer>();
//            System.out.println("Iu_p的交互次数：");
            for(Entry<Integer, Double> i4:sorted_r_unknow_item) {
                if(i4.getValue() >= threshold_c2)
                {
//                	System.out.print(i4.getValue() + "\t");
                    Iu_p.add(i4.getKey());                                   // === Iu_p
                }
            }
//            System.out.println();
            Iu_p_data.put(u, Iu_p);
//             System.out.println("Iu_p: " + Iu_p.size() + "个   " + Iu_p);
//            List<Integer> Iu_pl = new ArrayList<Integer>(Iu_p_data.get(u));
//            System.out.println("Iu_pl: " + Iu_pl.size() + Iu_pl);

            // -----------------------Iu_uk--------------------------
            List<Integer> Iu_uk = new ArrayList<Integer>(Iu_unknow);
            Iu_uk.removeAll(Iu_p);                                           // === Iu_uk
            Iu_uk_data.put(u, Iu_uk);
            // System.out.println("Iu_uk: " + Iu_uk.size() + "个");

        }
      }
    }

	public static void split_i_u () throws Exception
    {
    	if(flagsplit_i_u) 
    	{
    	System.out.println("split:  split_i_u");

    	double [][] S = new double [n+1][n+1];
    	int n_ = 0;
    	
		BufferedReader br = new BufferedReader(new FileReader(fnSimilarity_i_u));
    	String line = null;    			
    	while((line = br.readLine())!= null) {
    		
    		String[] terms = line.split("\t");
    		
    		for(int i=0; i<terms.length; i++) {
    			
    			S[n_][i] = Double.parseDouble(terms[i]);
    		}
    		
    		n_++;
    	}br.close();   	
    	// System.out.println("矩阵第二行：" + Arrays.toString(S[1]));	
			   	
    	// -------------- 根据相似度预测交互次数并划分数据集 ----------
		HashMap<Integer, Double> u_s = new HashMap<Integer, Double>();  // 用于储存用户与其他所有用户对应的相似度值（每次循环完需要清除一下）
		List<Integer> similar_user = new ArrayList<Integer>();   // 用于储存每个用户对应的相似用户集合（每次循环完需要清除一下）

        for (int u = 1; u <= n; u++)
        {
			similar_user.clear();			
			u_s.clear();

			for(int u1 = 1; u1 <= n; u1++)
			{
				u_s.put(u1, S[u][u1]);   // 给对应的每个用户相似度带上用户序号，方便后面排序筛选
			}

			// ------------- 根据用户相似度排序，筛选出相似用户 ----------
			List<Map.Entry<Integer,Double>> sorted_user = new ArrayList<Map.Entry<Integer,Double>>(u_s.entrySet());
			Collections.sort(sorted_user, new Comparator<Map.Entry<Integer,Double>>()
			{
				public int compare( Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2 )
				{
					return o2.getValue().compareTo( o1.getValue() ); // 降序排序
				}
			});

		//    System.out.print("排序后: ");
        //    for(Entry<Integer, Double> i3:sorted_user) {
        //        System.out.print(i3.getValue() + " ,  ");
        //    }
        //    System.out.println("\n");

			int sum_num = 0;
            for(Entry<Integer, Double> i4:sorted_user) {
                if(i4.getValue() >= threshold_u)
                {
					sum_num += 1;
                    similar_user.add(i4.getKey());                                   
                }
            }
//			 System.out.println("相似用户集合大小：" + sum_num);
			// ------------------------------------------------------

            HashMap<Integer, Double> r_unknow_item = new HashMap<Integer, Double>();     // 用于储存填充后的unknown物品交互情况

            // ------------------------Iu_i---------------------------
            List<Integer> Iu_i = new ArrayList<Integer>(TrainData.get(u).keySet());    // === Iu_i
            int Size_Iu_i = Iu_i.size();                                               // 用户u交互物品集数量
//            System.out.println("Iu_i:" + Size_Iu_i + "个 " + Iu_i);
            List<Integer> Iu_i_count = new ArrayList<Integer>(TrainData.get(u).values()); // 取出用户u交互过的物品集相应的交互次数
//            System.out.println("Iu_i_count:" + Iu_i_count);
            int sum = 0;
            for(Integer c:Iu_i_count) {
                sum += c;                                                              // 计算用户u交互物品次数总和
            }

            // ------------------------Iu_j---------------------------
            List<Integer> Iu_j = new ArrayList<Integer>(ItemSetDislike);               // 取出大家都不喜欢的物品集Idislike
//            System.out.println("Itemsetdislike: " + ItemSetDislike.size() + "个 " + ItemSetDislike);
            Iu_j.removeAll(Iu_i);                                                      // === Iu_j
            Iu_j_data.put(u, Iu_j);
            int Size_Iu_j = Iu_j.size();
//            System.out.println("Iu_j: " + Size_Iu_j + "个 " + Iu_j);

            // ----------------------Iu_*(Iu_unknow)------------------
            List<Integer> Iu_unknow = new ArrayList<Integer>(ItemSetWhole);            // 取出train中的所有物品集
//            System.out.println("Itemsetwhole: " + ItemSetWhole.size() + "个");
            Iu_unknow.removeAll(Iu_i);
            Iu_unknow.removeAll(Iu_j);                                                 // === Iu_unknow(Iu_*)
            int Size_Iu_unknow = Iu_unknow.size();                                     // Iu_*的大小
//            System.out.println("Iu_*: " + Size_Iu_unknow + "个  " + Iu_unknow);

            // -------------------------预测交互次数--------------------
			for(Integer i:Iu_unknow)
			{
				double r=0, s_sum=0;
				for(Integer uu:similar_user)      // 遍历相似用户
				{
					s_sum += S[u][uu];            // 所有权重和(分母)
					if(Data.get(uu).get(i) == null)
					{
						r += 0;  // (分子)
					}
					else
					{
						r += S[u][uu] * Data.get(uu).get(i);  // 用户相似度sim(u,uu)作为权重，乘上用户uu对物品i的交互次数(分子)
					}
				}
				r = r/s_sum;
				// System.out.println(r);
            	r_unknow_item.put(i, r);
			}

            // -----------------------sort----------------------
            // --- (妙啊，带着ID对预测评分排序)
            List<Map.Entry<Integer,Double>> sorted_r_unknow_item =
                    new ArrayList<Map.Entry<Integer,Double>>(r_unknow_item.entrySet());
            Collections.sort(sorted_r_unknow_item, new Comparator<Map.Entry<Integer,Double>>()
            {
                public int compare( Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2 )
                {
                    return o2.getValue().compareTo( o1.getValue() ); // 降序排序
                }
            });
//            System.out.print("排序后: ");
//            int nnn =0;
//            for(Entry<Integer, Double> i3:sorted_r_unknow_item) {
//            	nnn +=1;
//            	if(nnn<=20) {
//            		System.out.print(i3.getValue() + " ,  ");
//            	}
//            }
//            System.out.println("\n");

            // -----------------------Iu_p---------------------------
            List<Integer> Iu_p = new ArrayList<Integer>();
//            System.out.println("Iu_p的交互次数：");
            for(Entry<Integer, Double> i4:sorted_r_unknow_item) {
                if(i4.getValue() >= threshold_c2)
                {
//                	System.out.print(i4.getValue() + "\t");
                    Iu_p.add(i4.getKey());                                   // === Iu_p
                }
            }
//            System.out.println();
            Iu_p_data.put(u, Iu_p);
//            System.out.println("Iu_p: " + Iu_p.size() + "个   " + Iu_p);
//            List<Integer> Iu_pl = new ArrayList<Integer>(Iu_p_data.get(u));
//            System.out.println("Iu_pl: " + Iu_pl.size() + Iu_pl);

            // -----------------------Iu_uk--------------------------
            List<Integer> Iu_uk = new ArrayList<Integer>(Iu_unknow);
            Iu_uk.removeAll(Iu_p);                                           // === Iu_uk
            Iu_uk_data.put(u, Iu_uk);
//            System.out.println("Iu_uk: " + Iu_uk.size() + "个");

        }
      }
    }

    public static void split_user() throws Exception{
        if(flagsplit_user){
            System.out.println("split:  split_user(纵向偏好)");
            double [][] S = new double [n+1][n+1];
            int n_ = 0;
            // ------- 读取物品相似度文件,把里面的相似度矩阵数据读取出来调用 ----------  
            BufferedReader br = new BufferedReader(new FileReader(fnSimilarity_u));	
            String line = null;    			
            while((line = br.readLine())!= null) {
                
                String[] terms = line.split("\t");
                
                for(int i=0; i<terms.length; i++) {
                    
                    S[n_][i] = Double.parseDouble(terms[i]);
                }   		
                n_++;
            }
            br.close();   	
            
            System.out.println("用户相似度矩阵S大小：" + "行: " + S.length + " 列: " + S[0].length);	
            
            // -------------------- 根据用户相似度划分数据集 --------------------------
            for (int i = 1; i <= m; i++)
            {
                //System.out.println("开始划分数据集的item：" + i);
                if (!TrainDataItem2User.containsKey(i))
                    continue;

                // ------------------------Ui_u---------------------------
                List<Integer> Ui_u = new ArrayList<Integer>(TrainDataItem2User.get(i).keySet());    // === Ui_u            
                List<Integer> Ui_u_count = new ArrayList<Integer>(TrainDataItem2User.get(i).values()); // 取出交互过物品i的所有用户的相应的交互次数           
                int sum = 0;
                for(Integer c:Ui_u_count) {
                    sum += c;                                                              // 计算交互物品次数总和,后面对计算权重有用
                }       
                // ----------------------Ui_unknow------------------
                List<Integer> Ui_unknow = new ArrayList<Integer>(UserSetWhole);            // 取出所有用户集合
                Ui_unknow.removeAll(Ui_u);                                                 // === Ui_unknow
                            
                // -------------------------similarity--------------------
                // --- caculate the similarity_u with weight
                HashMap<Integer, Double> similarity = new HashMap<Integer, Double>();  // 用于储存Iu_*物品ID及相应的加权相似度
                similarity.clear();
                
                for(Integer u1:Ui_u)
                {
                    double weight = TrainDataItem2User.get(i).get(u1);
                    weight = weight/sum;  // 计算对于i1来说的cosine相似度的权重
    //                System.out.println("weight: " + weight);

                    for(Integer u2:Ui_unknow)
                    {
                        // === 计算带权相似度
                        double cosine = S[u1][u2] * weight;
    //	        		System.out.println(cosine);
                        // --- 根据i2的物品ID值来储存其相似度值，并按权求和
                        if(similarity.containsKey(u2))
                        {
                            Double cos = similarity.get(u2);
                            cos += cosine;
                            similarity.put(u2, cos);
                        }
                        else
                        {
                            similarity.put(u2, cosine);
                        }
                    }
                }
                //System.out.println("similarity: " + similarity.size() + "个 " + similarity.values());

                // -----------------------sort----------------------
                // --- (妙啊，带着ID对预测评分排序)
                List<Map.Entry<Integer,Double>> sorted_similarity =
                        new ArrayList<Map.Entry<Integer,Double>>(similarity.entrySet());
                Collections.sort(sorted_similarity, new Comparator<Map.Entry<Integer,Double>>()
                {
                    public int compare( Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2 )
                    {
                        return o2.getValue().compareTo( o1.getValue() ); // 降序排序
                    }
                });
                    //System.out.print("排序后: ");
                    //for(Entry<Integer, Double> i3:sorted_similarity) {
                    //	System.out.print(i3.getValue() + " ,  ");
                    //}
                    //System.out.println("\n");

                // -----------------------Ui_l---------------------------
                List<Integer> Ui_l = new ArrayList<Integer>();
                for(Entry<Integer, Double> i4:sorted_similarity) {
                    if(i4.getValue() >= threshold_user)
                    {
                        Ui_l.add(i4.getKey());                                   // === Ui_l
                    }
                }
                Ui_l_data.put(i, Ui_l);

                // -----------------------Ui_v--------------------------
                List<Integer> Ui_v = new ArrayList<Integer>(Ui_unknow);
                Ui_v.removeAll(Ui_l);                                           // === Ui_v
                Ui_v_data.put(i, Ui_v);
                // --------- 输出验证划分结果 ----------
    //            System.out.println("Ui_u: " + Ui_u.size() + "个");
    //            System.out.println("Ui_u: " + Ui_u);
    //            System.out.println("Ui_l: " + Ui_l.size() + "个");
    //            System.out.println("Ui_l: " + Ui_l);
    //            System.out.println("Ui_v: " + Ui_v.size() + "个");

            }
        }
    }

    // ========================== msbpr_i的train =========================
	public static void train_i()
	{
        System.out.println("trian_i");
		float pre_total_loss = 0f;
		float total_loss = 0f;
        for (int iter = 1; iter <= num_iterations; iter++)
        {
		for (int iter_rand = 1; iter_rand <= n; iter_rand++)
		{
			// ------------------------------- u --------------------------------
			// --- randomly sample a user $u$, Math.random(): [0.0, 1.0)
			int u = (int) Math.floor(Math.random() * n) + 1; // 随机抽一个用户的索引值
			if (!TrainData.containsKey(u))
				continue;
			// ==================================================================
			// ------------------------------ Iu_i -------------------------------
			List<Integer> Iu_i = new ArrayList<Integer>(TrainData.get(u).keySet());    // === Iu_i
			int Size_Iu_i = Iu_i.size();
			// --- randomly sample an item $i$, Math.random(): [0.0, 1.0)
			int t = (int) Math.floor(Math.random()*Size_Iu_i);
			int i = Iu_i.get(t);                                                       // === i
			// ----------------------------- Iu_j --------------------------------
			List<Integer> Iu_j = new ArrayList<Integer>(Iu_j_data.get(u));			   // === Iu_j
			int Size_Iu_j = Iu_j.size();
			// ----------------------------- Iu_p --------------------------------
			List<Integer> Iu_p = new ArrayList<Integer>(Iu_p_data.get(u));     		   // === Iu_p
			int Size_Iu_p = Iu_p.size();
			// ----------------------------- Iu_uk --------------------------------
			List<Integer> Iu_uk = new ArrayList<Integer>(Iu_uk_data.get(u));           // === Iu_uk
			int Size_Iu_uk = Iu_uk.size();
			// =========================================================
			// ======== 对于用户u：考虑其item可划分的情况有四种 ===========
			// [1] item 可划分成i,p,c,j; 4对偏序对: Iu_i>Iu_uk, Iu_i>Iu_j, Iu_p>Iu_uk, Iu_p>Iu_j
			// [2] item 可划分成i,c,j; 2对偏序对: Iu_i>Iu_uk, Iu_i>Iu_j
			// [3] item 可划分成i,p,c; 2对偏序对: Iu_i>Iu_uk, Iu_p>Iu_uk
			// [4] item 可划分成i,c; 1对偏序对: Iu_i>Iu_uk(此时退化成了bpr)
			// =========================================================
			if (Size_Iu_p == 0 && Size_Iu_j == 0){ 
				total_loss += train_ic(Size_Iu_uk, Iu_uk, u, i);                                        // 情况[4]
			}
			else if (Size_Iu_p != 0 && Size_Iu_j == 0){
				total_loss += train_ipc(Size_Iu_p, Iu_p, Size_Iu_uk, Iu_uk, u, i);                      // 情况[3]
			}
			else if (Size_Iu_p == 0 && Size_Iu_j != 0){
				total_loss += train_icj(Size_Iu_j, Iu_j, Size_Iu_uk, Iu_uk, u, i);                      // 情况[2]
			}
			else {
				total_loss += train_ipcj(Size_Iu_j, Iu_j, Size_Iu_p, Iu_p, Size_Iu_uk, Iu_uk, u, i);    // 情况[1]
			}
		}
		if (iter % 100 == 0) {
			total_loss = -total_loss / (float) n / (float) 100;
			System.out.println("iter " + iter + ", total_loss: " + total_loss + "   loss减小程度： " + (pre_total_loss - total_loss));
			pre_total_loss = total_loss;
			total_loss = 0f;
		} 
        }		
	}
	// -------------------------- 物品横向划分 --------------------------
	public static float train_ic(Integer Size_Iu_uk, List<Integer> Iu_uk, Integer u, Integer i){
		float loss_ic = 0;
		// --- randomly sample an item $uk$
		int t4 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int uk = Iu_uk.get(t4);                                                // === uk
		// ----------------------------------------------------------------------------
		// --- calculate loss_
		float r_ui = biasV[i];
		float r_uk = biasV[uk];
		for (int f=0; f<d; f++){
			r_ui += U[u][f] * V[i][f];			
			r_uk += U[u][f] * V[uk][f];	
		}
		float r_Xuik = r_ui - r_uk;
		float loss_uik =  1f / (1f + (float) Math.pow(Math.E, r_Xuik));
		// --------------------------------------------------------------------------
		// --- update $U_{w\cdot}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( loss_uik * (V[i][f] - V[uk][f]) - alpha_u * U[u][f] );
		}
		// ---------------------------------------------------
		// --- update $V_{i\cdot}$
		for (int f=0; f<d; f++)
		{
			V[i][f] += gamma * ( loss_uik * U[u][f] - alpha_v * V[i][f] );
		}
		// --- update $V_{uk\cdot}$
		for (int f=0; f<d; f++)
		{
			V[uk][f] += gamma * ( loss_uik * U[u][f] * (-1) - alpha_v * V[uk][f] );
		}
		// --- update $b_i$
		biasV[i] += gamma * ( loss_uik - beta_v * biasV[i] );
		// --- update $b_uk$
		biasV[uk] += gamma * ( loss_uik * (-1) - beta_v * biasV[uk] );
		// ---------------------------------------------------
		float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuik)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(V[i])+norm(V[uk])+biasV[i]*biasV[i]+biasV[uk]*biasV[uk]));
		loss_ic = bpr_loss - reg_loss;

		return loss_ic;
	}

	public static float train_ipc(Integer Size_Iu_p, List<Integer> Iu_p, Integer Size_Iu_uk, List<Integer> Iu_uk, Integer u, Integer i){
		float loss_ipc = 0;
		// --- randomly sample an item $l$
		int t3 = (int) Math.floor(Math.random()*Size_Iu_p);
		int l = Iu_p.get(t3);                                                  // === l
		// --- randomly sample an item $uk$
		int t4 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int uk = Iu_uk.get(t4);                                                // === uk
		// ------------------------------------------------------------------------------------------------
		// --- calculate loss_
		float r_ui = biasV[i];
		float r_ul = biasV[l];
		float r_uk = biasV[uk];		
		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_ul += U[u][f] * V[l][f];
			r_uk += U[u][f] * V[uk][f];
		}
		float r_Xuik = r_ui - r_uk;
		float r_Xulk = r_ul - r_uk;

		float loss_uik =  1f / (1f + (float) Math.pow(Math.E, r_Xuik) );
		float loss_ulk =  1f / (1f + (float) Math.pow(Math.E, r_Xulk) );
		// --------------------------------------------------------------------------
		// --- update $U_{w\cdot}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( loss_uik * (V[i][f] - V[uk][f]) + loss_ulk * (V[l][f] - V[uk][f]) - alpha_u * U[u][f] );
		}
		// ---------------------------------------------------
		// --- update $V_{i\cdot}$
		for (int f=0; f<d; f++)
		{
			V[i][f] += gamma * ( loss_uik * U[u][f] - alpha_v * V[i][f] );
		}
		// --- update $V_{l\cdot}$
		for (int f=0; f<d; f++)
		{
			V[l][f] += gamma * ( loss_ulk * U[u][f] - alpha_v * V[l][f] );
		}
		// --- update $V_{uk\cdot}$
		for (int f=0; f<d; f++)
		{
			V[uk][f] = V[uk][f] + gamma * ( (loss_uik + loss_ulk) * U[u][f] * (-1) - alpha_v * V[uk][f] );
		}
		// ---------------------------------------------------
		// --- update $b_i$
		biasV[i] += gamma * ( loss_uik - beta_v * biasV[i] );
		// --- update $b_l$
		biasV[l] += gamma * ( loss_ulk - beta_v * biasV[l] );
		// --- update $b_uk$
		biasV[uk] += gamma * ( (loss_uik + loss_ulk) * (-1) - beta_v * biasV[uk] );
		// ---------------------------------------------------
		float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuik)))+(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xulk)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(V[i])+norm(V[l])+norm(V[uk])+biasV[i]*biasV[i]+biasV[l]*biasV[l]+biasV[uk]*biasV[uk]));
		loss_ipc = bpr_loss - reg_loss;

		return loss_ipc;
	}

	public static float train_icj(Integer Size_Iu_j, List<Integer> Iu_j, Integer Size_Iu_uk, List<Integer> Iu_uk, Integer u, Integer i){
		float loss_icj = 0;
		// --- randomly sample an item $j$
		int t1 = (int) Math.floor(Math.random()*Size_Iu_j);
		int j = Iu_j.get(t1);                                                  // === j
		// --- randomly sample an item $uk$
		int t2 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int uk = Iu_uk.get(t2);                                                // === uk
		// -------------------------------------------------------------------------------------
		// ---calculate loss_
		float r_ui = biasV[i];
		float r_uk = biasV[uk];
		float r_uj = biasV[j];
		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_uk += U[u][f] * V[uk][f];
			r_uj += U[u][f] * V[j][f];
		}
		float r_Xuik = r_ui - r_uk;
		float r_Xuij = r_ui - r_uj;				

		float loss_uik =  1f / (1f + (float) Math.pow(Math.E, r_Xuik) );
		float loss_uij =  1f / (1f + (float) Math.pow(Math.E, r_Xuij) );	

		// ---------------------------------------------------------------------
		// --- update $U_{w\cdot}$
		for(int f=0; f<d; f++)
		{
			U[u][f] = U[u][f] + gamma * ( loss_uik * (V[i][f] - V[uk][f]) + loss_uij * (V[i][f] - V[j][f]) - alpha_u * U[u][f] );
		}
		// ---------------------------------------------------
		// --- update $V_{i\cdot}$
		for (int f=0; f<d; f++)
		{
			V[i][f] = V[i][f] + gamma * ( (loss_uik + loss_uij) * U[u][f] - alpha_v * V[i][f] );
		}
		// --- update $V_{uk\cdot}$
		for (int f=0; f<d; f++)
		{
			V[uk][f] = V[uk][f] + gamma * ( loss_uik * U[u][f] * (-1) - alpha_v * V[uk][f] );
		}
		// --- update $V_{j\cdot}$
		for (int f=0; f<d; f++)
		{
			V[j][f] = V[j][f] + gamma * ( loss_uij * U[u][f] * (-1) - alpha_v * V[j][f] );
		}
		// ---------------------------------------------------
		// --- update $b_i$
		biasV[i] = biasV[i] + gamma * ( loss_uik + loss_uij - beta_v * biasV[i] );
		// --- update $b_uk$
		biasV[uk] = biasV[uk] + gamma * ( loss_uik * (-1) - beta_v * biasV[uk] );
		// --- update $b_j$
		biasV[j] = biasV[j] + gamma * ( loss_uij * (-1) - beta_v * biasV[j] );
		// ---------------------------------------------------
		float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuik))) + (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuij)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(V[i])+norm(V[uk])+norm(V[j])+biasV[i]*biasV[i]+biasV[uk]*biasV[uk]+biasV[j]*biasV[j]));
		loss_icj = bpr_loss - reg_loss;

		return loss_icj;
	}
	
	public static float train_ipcj(Integer Size_Iu_j, List<Integer> Iu_j, Integer Size_Iu_p, List<Integer> Iu_p, Integer Size_Iu_uk, List<Integer> Iu_uk, Integer u, Integer i){
		float loss_ipcj = 0;
		// --- randomly sample an item $j$
		int t1 = (int) Math.floor(Math.random()*Size_Iu_j);
		int j = Iu_j.get(t1);                                                  // === j
		// --- randomly sample an item $l$
		int t3 = (int) Math.floor(Math.random()*Size_Iu_p);
		int l = Iu_p.get(t3);                                                  // === l
		// --- randomly sample an item $uk$
		int t4 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int uk = Iu_uk.get(t4);                                                // === uk
		// ------------------------------------------------------------------------------------------------
		// --- calculate loss_
		float r_ui = biasV[i];
		float r_ul = biasV[l];
		float r_uk = biasV[uk];
		float r_uj = biasV[j];
		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_ul += U[u][f] * V[l][f];
			r_uk += U[u][f] * V[uk][f];
			r_uj += U[u][f] * V[j][f];
		}
		float r_Xuik = r_ui - r_uk;
		float r_Xuij = r_ui - r_uj;
		float r_Xulk = r_ul - r_uk;
		float r_Xulj = r_ul - r_uj;

		float loss_uik =  1f / (1f + (float) Math.pow(Math.E, r_Xuik) );
		float loss_uij =  1f / (1f + (float) Math.pow(Math.E, r_Xuij) );
		float loss_ulk =  1f / (1f + (float) Math.pow(Math.E, r_Xulk) );
		float loss_ulj =  1f / (1f + (float) Math.pow(Math.E, r_Xulj) );
		// --------------------------------------------------------------------------
		// --- update $U_{w\cdot}$
		for(int f=0; f<d; f++)
		{
			U[u][f] = U[u][f] + gamma * ( loss_uik * (V[i][f] - V[uk][f]) + loss_uij * (V[i][f] - V[j][f])  + loss_ulk * (V[l][f] - V[uk][f]) + loss_ulj * (V[l][f] - V[j][f])- alpha_u * U[u][f] );
		}
		// ---------------------------------------------------
		// --- update $V_{i\cdot}$
		for (int f=0; f<d; f++)
		{
			V[i][f] = V[i][f] + gamma * ( (loss_uik + loss_uij) * U[u][f] - alpha_v * V[i][f] );
		}
		// --- update $V_{l\cdot}$
		for (int f=0; f<d; f++)
		{
			V[l][f] = V[l][f] + gamma * ( (loss_ulk + loss_ulj) * U[u][f] - alpha_v * V[l][f] );
		}
		// --- update $V_{uk\cdot}$
		for (int f=0; f<d; f++)
		{
			V[uk][f] = V[uk][f] + gamma * ( (loss_uik + loss_ulk) * U[u][f] * (-1) - alpha_v * V[uk][f] );
		}
		// --- update $V_{j\cdot}$
		for (int f=0; f<d; f++)
		{
			V[j][f] = V[j][f] + gamma * ( (loss_uij + loss_ulj) * U[u][f] * (-1) - alpha_v * V[j][f] );
		}
		// ---------------------------------------------------
		// --- update $b_i$
		biasV[i] = biasV[i] + gamma * ( loss_uik + loss_uij - beta_v * biasV[i] );
		// --- update $b_l$
		biasV[l] = biasV[l] + gamma * ( loss_ulk + loss_ulj - beta_v * biasV[l] );
		// --- update $b_uk$
		biasV[uk] = biasV[uk] + gamma * ( (loss_uik + loss_ulk) * (-1) - beta_v * biasV[uk] );
		// --- update $b_j$
		biasV[j] = biasV[j] + gamma * ( (loss_uij + loss_ulj) * (-1) - beta_v * biasV[j] );
		// ---------------------------------------------------
		float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuik)))+(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuij)))+(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xulk)))+(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xulj)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(V[i])+norm(V[l])+norm(V[uk])+norm(V[j])+biasV[i]*biasV[i]+biasV[l]*biasV[l]+biasV[uk]*biasV[uk]+biasV[j]*biasV[j]));
		loss_ipcj = bpr_loss - reg_loss;
		// ===================================================
		return loss_ipcj;
	}
	// ========================== msbpr_u的train =============================
    public static void train_u()
	{
        System.out.println("trian_u");
		float pre_total_loss = 0f;
		float total_loss = 0f;
        for (int iter = 1; iter <= num_iterations; iter++)
        {
		for (int iter_rand = 1; iter_rand <= m; iter_rand++)
		{
			// ------------------------------- i --------------------------------
			// --- randomly sample a item $i$, Math.random(): [0.0, 1.0)
			int i = (int) Math.floor(Math.random() * m) + 1; // 随机抽一个物品的索引值
			if (!TrainDataItem2User.containsKey(i))
				continue;
			// ==================================================================
            // ------------------------------ Ui_u -------------------------------
			List<Integer> Ui_u = new ArrayList<Integer>(TrainDataItem2User.get(i).keySet());    // === Ui_u
			int Size_Ui_u = Ui_u.size();
			// --- randomly sample an user $u$, Math.random(): [0.0, 1.0)
			int t = (int) Math.floor(Math.random()*Size_Ui_u);
			int u = Ui_u.get(t);                                                       // === u

            // ----------------------------- Ui_l --------------------------------
			List<Integer> Ui_l = new ArrayList<Integer>(Ui_l_data.get(i));     		   // === Ui_l
			int Size_Ui_l = Ui_l.size();

			// ----------------------------- Ui_v --------------------------------
			List<Integer> Ui_v = new ArrayList<Integer>(Ui_v_data.get(i));           // === Ui_v
			int Size_Ui_v = Ui_v.size();

			// =========================================================
			// ======== 对于用户i：考虑其user可划分的情况有2种 ===========
			// [1] user 可划分成u,l,v; 2对偏序对: Ui_u > Ui_v, Ui_l > Ui_v
			// [2] user 可划分成u,v; 1对偏序对: Ui_u > Ui_c(此时退化为bpr)
			// =========================================================
			if (Size_Ui_l == 0){ 
				total_loss += train_u_uv(Size_Ui_v, Ui_v, u, i);                                        // 情况[2]
			}
			else {
				total_loss += train_u_ulv(Size_Ui_v, Ui_v, Size_Ui_l, Ui_l, u, i);    // 情况[1]
			}
		}
		if (iter % 100 == 0) {
			total_loss = -total_loss / (float) n / (float) 100;
			System.out.println("iter " + iter + ", total_loss: " + total_loss + "   loss减小程度： " + (pre_total_loss - total_loss));
			pre_total_loss = total_loss;
			total_loss = 0f;
		} 
        }
	}
	// -------------------------- 用户纵向划分 --------------------------
    public static float train_u_uv(Integer Size_Ui_v, List<Integer> Ui_v, Integer u, Integer i){
        float loss_uv = 0;
        // --- randomly sample an item $v$
        int t2 = (int) Math.floor(Math.random()*Size_Ui_v);
        int v = Ui_v.get(t2);                                        // === v
        // ----------------------------------------------------------------------
        // ---calculate loss_
        float r_ui = biasU[u];
        float r_vi = biasU[v];
        
        for (int f=0; f<d; f++)
        {
            r_ui += U[u][f] * V[i][f];
            r_vi += U[v][f] * V[i][f];
        }
        float r_Xiuv = r_ui - r_vi;			
        float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );							
        // ---------------------------------------------------------------------
        // --- update $U_{u\cdot}$
        for(int f=0; f<d; f++)
        {
            U[u][f] += gamma * ( loss_iuv * V[i][f] - alpha_u * U[u][f] );
        }
        // --- update $U_{v\cdot}$
        for(int f=0; f<d; f++)
        {
            U[v][f] += gamma * ( loss_iuv * (-1) * V[i][f] - alpha_u * U[v][f] );
        }
        // ---------------------------------------------------
        // --- update $V_{i\cdot}$
        for (int f=0; f<d; f++)
        {
            V[i][f] += gamma * ( loss_iuv * (U[u][f] - U[v][f]) - alpha_v * V[i][f] );
        }
        // ---------------------------------------------------
        // --- update $b_u$
        biasU[u] += gamma * ( loss_iuv  - beta_v * biasU[u] );
        // --- update $b_c$
        biasU[v] += gamma * ( loss_iuv * (-1) - beta_v * biasU[v] );
        // ---------------------------------------------------
		float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(V[i])+norm(V[v])+biasV[i]*biasV[i]+biasV[v]*biasV[v]));
		loss_uv = bpr_loss - reg_loss;

		return loss_uv;
    }

    public static float train_u_ulv(Integer Size_Ui_v, List<Integer> Ui_v, Integer Size_Ui_l, List<Integer> Ui_l, Integer u, Integer i){
        float loss_ulv = 0;
        // --- randomly sample an user $l$
        int t3 = (int) Math.floor(Math.random()*Size_Ui_l);
        int l = Ui_l.get(t3);                                                  // === l

        // --- randomly sample an item $uk$
        int t4 = (int) Math.floor(Math.random()*Size_Ui_v);
        int v = Ui_v.get(t4);                                                // === c

        // ------------------------------------------------------------------------------------------------
        // --- calculate loss_
        float r_ui = biasU[u];
        float r_li = biasU[l];
        float r_vi = biasU[v];
        for (int f=0; f<d; f++)
        {
            r_ui += U[u][f] * V[i][f];
            r_li += U[l][f] * V[i][f];
            r_vi += U[v][f] * V[i][f];
        }
        float r_Xiuv = r_ui - r_vi;
        float r_Xilv = r_li - r_vi;
        
        float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
        float loss_ilv =  1f / (1f + (float) Math.pow(Math.E, r_Xilv) );
        // --------------------------------------------------------------------------
        // --- update $V_{i\cdot}$
        for(int f=0; f<d; f++)
        {
            V[i][f] +=  gamma * ( loss_iuv * (U[u][f] - U[v][f])  + loss_ilv * (U[l][f] - U[v][f]) - alpha_v * V[i][f] );
        }
        // ---------------------------------------------------
        // --- update $U_{u\cdot}$
        for(int f=0; f<d; f++)
        {
            U[u][f] += gamma * ( loss_iuv * V[i][f] - alpha_u * U[u][f] );
        }
        // --- update $U_{l\cdot}$
        for(int f=0; f<d; f++)
        {
            U[l][f] += gamma * ( loss_ilv * V[i][f] - alpha_u * U[l][f] );
        }
        // --- update $U_{v\cdot}$
        for(int f=0; f<d; f++)
        {
            U[v][f] += gamma * ( -1 * (loss_iuv + loss_ilv) * V[i][f] - alpha_u * U[v][f] );
        }
        // ---------------------------------------------------
        // --- update $b_u$
        biasU[u] += gamma * ( loss_iuv - beta_v * biasU[u] );
        // --- update $b_l$
        biasU[l] += gamma * ( loss_ilv - beta_v * biasU[l] );
        // --- update $b_v$
        biasU[v] += gamma * ( -1 * (loss_ilv + loss_iuv) - beta_v * biasU[v] );
        // ---------------------------------------------------
        float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv))) + (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xilv)));
        float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[v])+norm(U[l])+norm(V[i])+biasU[u]*biasU[u]+biasU[v]*biasU[v]+biasU[l]*biasU[l]));
        loss_ulv = bpr_loss - reg_loss;

        return loss_ulv;
    }
	// ================================================================================================================
	// ========================== 模型融合的train =========================
	// == bpr + bpr_u(横不划，纵不划)
	public static void train_i_u(){
		System.out.println("trian_i_u: bpr + bpr_u");
        float pre_total_loss = 0f;
		float total_loss = 0f;
        for (int iter = 1; iter <= num_iterations; iter++)
        {
            for (int iter_rand = 0; iter_rand < m; iter_rand++)
            {
                // --- randomly sample a item $i$, Math.random(): [0.0, 1.0)
			    int i = (int) Math.floor(Math.random() * m) + 1; // 随机抽一个物品的索引值             // === i
                if (!TrainDataItem2User.containsKey(i))
				continue;
                // ============================纵向偏好多元组==============================
                // ------------------------------ Ui_u -------------------------------
                List<Integer> Ui_u = new ArrayList<Integer>(TrainDataItem2User.get(i).keySet());    // === Ui_u
                int Size_Ui_u = Ui_u.size();
                // --- randomly sample an user $u$, Math.random(): [0.0, 1.0)
                int t = (int) Math.floor(Math.random()*Size_Ui_u);
                int u = Ui_u.get(t);                                                                // === u

                List<Integer> ItemSet = new ArrayList<Integer>(TrainData.get(u).keySet());  
                List<Integer> UserSet = new ArrayList<Integer>(TrainDataItem2User.get(i).keySet()); 
				// ----------------------------- Ui_v --------------------------------
				// --- randomly sample an user $v$	    		
				int v = 0;
				do
				{
					v = (int) Math.floor(Math.random() * n) + 1;	    			               // === v
				}while( !UserSetWhole.contains(v) || UserSet.contains(v) );
                // ============================横向偏好二元组==============================
                // ------------------------------- j --------------------------------
                // --- randomly sample an item $j$	    		
                int j = 0;
                do
                { 
                    j = (int) Math.floor(Math.random() * m) + 1;	    			               // === j
                }while( !ItemSetWhole.contains(j) || ItemSet.contains(j) );	 
                // =========================================================
                total_loss += train_ij_uv_(u, v, i, j);                   
            }
			if (iter % 100 == 0) {
				total_loss = -total_loss / (float) n / (float) 100;
				System.out.println("iter " + iter + ", total_loss: " + total_loss + "   loss减小程度： " + (pre_total_loss - total_loss));
				pre_total_loss = total_loss;
				total_loss = 0f;
			} 
        }
	}

	// == bpr + msbpr_u(横不划，纵划)
    public static void train_i_ulv(){
        System.out.println("trian_i_ulv: bpr + msbpr_u");
        float pre_total_loss = 0f;
		float total_loss = 0f;
        for (int iter = 1; iter <= num_iterations; iter++)
        {
            for (int iter_rand = 0; iter_rand < m; iter_rand++)
            {
                // --- randomly sample a item $i$, Math.random(): [0.0, 1.0)
			    int i = (int) Math.floor(Math.random() * m) + 1; // 随机抽一个物品的索引值             // === i
                if (!TrainDataItem2User.containsKey(i))
				continue;
                // ============================纵向偏好多元组==============================
                // ------------------------------ Ui_u -------------------------------
                List<Integer> Ui_u = new ArrayList<Integer>(TrainDataItem2User.get(i).keySet());    // === Ui_u
                int Size_Ui_u = Ui_u.size();
                // --- randomly sample an user $u$, Math.random(): [0.0, 1.0)
                int t = (int) Math.floor(Math.random()*Size_Ui_u);
                int u = Ui_u.get(t);                                                                // === u
                List<Integer> ItemSet = new ArrayList<Integer>(TrainData.get(u).keySet());  
                                    
                // ----------------------------- Ui_l --------------------------------
                List<Integer> Ui_l = new ArrayList<Integer>(Ui_l_data.get(i));     		            // === Ui_l
                int Size_Ui_l = Ui_l.size();

                // ----------------------------- Ui_v --------------------------------
                List<Integer> Ui_v = new ArrayList<Integer>(Ui_v_data.get(i));                      // === Ui_v
                int Size_Ui_v = Ui_v.size();
                // ============================横向偏好二元组==============================
                // ------------------------------- j --------------------------------
                // --- randomly sample an item $j$	    		
                int j = 0;
                do
                { 
                    j = (int) Math.floor(Math.random() * m) + 1;	    			
                }while( !ItemSetWhole.contains(j) || ItemSet.contains(j) );	 
                // =========================================================
                // ======== 对于物品 i：考虑其user可划分的情况有两种 ===========
                // [1] user 可划分成u,l,v; 3对偏序对: Iu_i>Iu_j; Iu_i>Iv_i, Il_i>Iv_i
                // [2] user 可划分成u,v; 2对偏序对: Iu_i>Iu_j; Iu_i>Iv_i
                // =========================================================
                if (Size_Ui_l == 0){ 
                    total_loss += train_ij_uv(Size_Ui_v, Ui_v, u, i, j);                   // 情况[2]
                }
                else{
                    total_loss += train_ij_ulv(Size_Ui_l, Ui_l, Size_Ui_v, Ui_v, u, i, j);      // 情况[1]
                }
            }
			if (iter % 100 == 0) {
				total_loss = -total_loss / (float) n / (float) 100;
				System.out.println("iter " + iter + ", total_loss: " + total_loss + "   loss减小程度： " + (pre_total_loss - total_loss));
				pre_total_loss = total_loss;
				total_loss = 0f;
			} 
        }
    }
    
	// == msbpr + bpr_u(横划，纵不划)
	public static void train_ipcj_u(){
		System.out.println("trian_ipcj_u: msbpr + bpr_u");
		float pre_total_loss = 0f;
		float total_loss = 0f;
		for (int iter = 1; iter <= num_iterations; iter++){
			for (int iter_rand = 0; iter_rand < n; iter_rand++){
				// ------------------------------- u --------------------------------
				// --- randomly sample a user $u$, Math.random(): [0.0, 1.0)
				int u = (int) Math.floor(Math.random() * n) + 1; // 随机抽一个用户的索引值   // === u
				if (!TrainData.containsKey(u))
					continue;
				// ============================横向偏好多元组==========================
				// ------------------------------ Iu_i -------------------------------
				List<Integer> Iu_i = new ArrayList<Integer>(TrainData.get(u).keySet());    // === Iu_i
				int Size_Iu_i = Iu_i.size();
				// --- randomly sample an item $i$, Math.random(): [0.0, 1.0)
				int t = (int) Math.floor(Math.random()*Size_Iu_i);
				int i = Iu_i.get(t);                                                       // === i
				// ----------------------------- Iu_j --------------------------------
				List<Integer> Iu_j = new ArrayList<Integer>(Iu_j_data.get(u));			   // === Iu_j
				int Size_Iu_j = Iu_j.size();
				// ----------------------------- Iu_p --------------------------------
				List<Integer> Iu_p = new ArrayList<Integer>(Iu_p_data.get(u));     		   // === Iu_p
				int Size_Iu_p = Iu_p.size();
				// ----------------------------- Iu_uk --------------------------------
				List<Integer> Iu_uk = new ArrayList<Integer>(Iu_uk_data.get(u));           // === Iu_uk
				int Size_Iu_uk = Iu_uk.size();
				// ============================纵向偏好多元组==========================
				List<Integer> UserSet = new ArrayList<Integer>(TrainDataItem2User.get(i).keySet()); 
				// ----------------------------- Ui_v --------------------------------
				// --- randomly sample an user $v$	    		
				int v = 0;
				do
				{
					v = (int) Math.floor(Math.random() * n) + 1;	    			
				}while( !UserSetWhole.contains(v) || UserSet.contains(v) );	 
				// =========================================================
				// ======== 对于用户u：考虑其item可划分的情况有四种 ===========
				// [1] item 可划分成i,p,c,j; 5对偏序对: Iu_i>Iu_uk, Iu_i>Iu_j, Iu_p>Iu_uk, Iu_p>Iu_j; Ui_u>Ui_v
				// [2] item 可划分成i,c,j; 3对偏序对: Iu_i>Iu_uk, Iu_i>Iu_j; Ui_u>Ui_v
				// [3] item 可划分成i,p,c; 3对偏序对: Iu_i>Iu_uk, Iu_p>Iu_uk; Ui_u>Ui_v
				// [4] item 可划分成i,c; 2对偏序对: Iu_i>Iu_uk(此时退化成了bpr); Ui_u>Ui_v
				// =========================================================
				if (Size_Iu_p == 0 && Size_Iu_j == 0){
					total_loss += train_ic_uv(Size_Iu_uk, Iu_uk, u, i, v);                                     // 情况[4]
				}
				else if (Size_Iu_p != 0 && Size_Iu_j == 0){
					total_loss += train_ipc_uv(Size_Iu_p, Iu_p, Size_Iu_uk, Iu_uk, u, i, v);       		       // 情况[3]
				}
				else if (Size_Iu_p == 0 && Size_Iu_j != 0){
					total_loss += train_icj_uv(Size_Iu_uk, Iu_uk, Size_Iu_j, Iu_j, u, i, v);   			       // 情况[2]
				}
				else{ 
					total_loss += train_ipcj_uv(Size_Iu_p, Iu_p, Size_Iu_uk, Iu_uk, Size_Iu_j, Iu_j, u, i, v); // 情况[1]
				}
			}
			if (iter % 100 == 0) {
				total_loss = -total_loss / (float) n / (float) 100;
				System.out.println("iter " + iter + ", total_loss: " + total_loss + "   loss减小程度： " + (pre_total_loss - total_loss));
				pre_total_loss = total_loss;
				total_loss = 0f;
			} 
	  	}
	}
	
	// == msbpr + msbpr_u(横划， 纵划)
	public static void train_ipcj_ulv(){
		System.out.println("trian_ipcj_ulv: msbpr + msbpr_u");
		float pre_total_loss = 0f;
		float total_loss = 0f;
		for (int iter = 1; iter <= num_iterations; iter++){
			for (int iter_rand = 0; iter_rand < n; iter_rand++){
				// ------------------------------- u --------------------------------
				// --- randomly sample a user $u$, Math.random(): [0.0, 1.0)
				int u = (int) Math.floor(Math.random() * n) + 1; // 随机抽一个用户的索引值   // === u
				if (!TrainData.containsKey(u))
					continue;
				// ============================横向偏好多元组==========================
				// ------------------------------ Iu_i -------------------------------
				List<Integer> Iu_i = new ArrayList<Integer>(TrainData.get(u).keySet());    // === Iu_i
				int Size_Iu_i = Iu_i.size();
				// --- randomly sample an item $i$, Math.random(): [0.0, 1.0)
				int t = (int) Math.floor(Math.random()*Size_Iu_i);
				int i = Iu_i.get(t);                                                       // === i
				// ----------------------------- Iu_j --------------------------------
				List<Integer> Iu_j = new ArrayList<Integer>(Iu_j_data.get(u));			   // === Iu_j
				int Size_Iu_j = Iu_j.size();
				// ----------------------------- Iu_p --------------------------------
				List<Integer> Iu_p = new ArrayList<Integer>(Iu_p_data.get(u));     		   // === Iu_p
				int Size_Iu_p = Iu_p.size();
				// ----------------------------- Iu_uk --------------------------------
				List<Integer> Iu_uk = new ArrayList<Integer>(Iu_uk_data.get(u));           // === Iu_uk
				int Size_Iu_uk = Iu_uk.size();
				// ============================纵向偏好多元组==========================
				// ----------------------------- Ui_l --------------------------------
				List<Integer> Ui_l = new ArrayList<Integer>(Ui_l_data.get(i));     		   // === Ui_l
				int Size_Ui_l = Ui_l.size();

				// ----------------------------- Ui_v --------------------------------
				List<Integer> Ui_v = new ArrayList<Integer>(Ui_v_data.get(i));           // === Ui_v
				int Size_Ui_v = Ui_v.size();
				// =========================================================
				// ======== 对于用户u和物品i：考虑其user和item可划分的情况有8种 ===========
				// [1] (i,p,c,j) + (u,l,v)
				// [2] (i,p,c,j) + (u,v)
				// [3] (i,c,j) + (u,l,v)
				// [4] (i,c,j) + (u,v)
				// [5] (i,p,c) + (u,l,v)
				// [6] (i,p,c) + (u,v)
				// [7] (i,c) + (u,l,v)
				// [8] (i,c) + (u,v)
				// =========================================================
				if (Size_Iu_p == 0 && Size_Iu_j == 0 && Size_Ui_l == 0){  								    // 情况[8]
					total_loss += train_ic_uv_(Size_Iu_uk, Iu_uk, u, i, Size_Ui_v, Ui_v);                   
				}
				else if (Size_Iu_p == 0 && Size_Iu_j == 0 && Size_Ui_l != 0){                               // 情况[7]
					total_loss += train_ic_ulv(Size_Iu_uk, Iu_uk, u, i, Size_Ui_l, Ui_l, Size_Ui_v, Ui_v);
				}
				else if (Size_Iu_p != 0 && Size_Iu_j == 0 && Size_Ui_l == 0){                               // 情况[6]
					total_loss += train_ipc_uv_(Size_Iu_p, Iu_p, Size_Iu_uk, Iu_uk, u, i, Size_Ui_v, Ui_v);
				}
				else if (Size_Iu_p != 0 && Size_Iu_j == 0 && Size_Ui_l != 0){                              // 情况[5]
					total_loss += train_ipc_ulv(Size_Iu_p, Iu_p, Size_Iu_uk, Iu_uk, u, i, Size_Ui_l, Ui_l, Size_Ui_v, Ui_v);
				}
				else if (Size_Iu_p == 0 && Size_Iu_j != 0 && Size_Ui_l == 0){                              // 情况[4]
					total_loss += train_icj_uv_(Size_Iu_uk, Iu_uk, Size_Iu_j, Iu_j, u, i, Size_Ui_v, Ui_v);   			   
				}
				else if (Size_Iu_p == 0 && Size_Iu_j != 0 && Size_Ui_l != 0){                              // 情况[3]
					total_loss += train_icj_ulv(Size_Iu_uk, Iu_uk, Size_Iu_j, Iu_j, u, i, Size_Ui_l, Ui_l, Size_Ui_v, Ui_v);
				}
				else if (Size_Iu_p != 0 && Size_Iu_j != 0 && Size_Ui_l == 0) {                             // 情况[2]                                                          // 情况[1]
					total_loss += train_ipcj_uv_(Size_Iu_p, Iu_p, Size_Iu_uk, Iu_uk, Size_Iu_j, Iu_j, u, i, Size_Ui_v, Ui_v);                 
				}
				else if (Size_Iu_p != 0 && Size_Iu_j != 0 && Size_Ui_l != 0){                              // 情况[1]
					total_loss += train_ipcj_ulv(Size_Iu_p, Iu_p, Size_Iu_uk, Iu_uk, Size_Iu_j, Iu_j, u, i, Size_Ui_l, Ui_l, Size_Ui_v, Ui_v);
				}
			} 
			if (iter % 100 == 0) {
				total_loss = -total_loss / (float) n / (float) 100;
				System.out.println("iter " + iter + ", total_loss: " + total_loss + "   loss减小程度： " + (pre_total_loss - total_loss));
				pre_total_loss = total_loss;
				total_loss = 0f;
			} 
	  	}
	}
	// =========================== 横向，纵向结合划分 ======================
	public static float train_ij_uv_(Integer u, Integer v, Integer i, Integer j){
        float loss_ij_uv = 0;
        // ---calculate loss_
        float r_ui = biasU[u] + biasV[i];
        float r_vi = biasU[v] + biasV[i];
        float r_uj = biasU[u] + biasV[j];
        
        for (int f=0; f<d; f++)
        {
            r_ui += U[u][f] * V[i][f];
            r_vi += U[v][f] * V[i][f];
            r_uj += U[u][f] * V[j][f];
        }
        float r_Xiuv = r_ui - r_vi;
        float r_Xuij = r_ui - r_uj;

        float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		float loss_uij =  1f / (1f + (float) Math.pow(Math.E, r_Xuij) );
        // ---------------------------------------------------------------------
        // --- update $U_{u\cdot}$
        for(int f=0; f<d; f++)
        {
            U[u][f] += gamma * ( loss_uij * (V[i][f] - V[j][f]) + loss_iuv * V[i][f]  - alpha_u * U[u][f] );
        }
        // --- update $U_{v\cdot}$
        for(int f=0; f<d; f++)
        {
            U[v][f] += gamma * ( loss_iuv * (-1) * V[i][f]  - alpha_u * U[v][f] );
        }
        // ---------------------------------------------------
        // --- update $V_{i\cdot}$
        for (int f=0; f<d; f++)
        {
            V[i][f] += gamma * ( loss_uij * U[u][f] + loss_iuv * (U[u][f] - U[v][f]) - alpha_v * V[i][f] );
        }
        // ---------------------------------------------------
        // --- update $b_u$
        biasU[u] += gamma * ( loss_iuv  - beta_v * biasU[u] );
        // --- update $b_c$
        biasU[v] += gamma * ( loss_iuv * (-1) - beta_v * biasU[v] );
        // --- update $b_i$
        biasV[i] += gamma * ( loss_uij - beta_v * biasV[i] );
        // --- update $b_j$
        biasV[j] += gamma * ( loss_uij * (-1) - beta_v * biasV[j] );
        // ---------------------------------------------------
        float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuij))) + (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[v])+norm(V[i])+norm(V[j])+biasU[u]*biasU[u]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[j]*biasV[j]));
		loss_ij_uv = bpr_loss - reg_loss;

		return loss_ij_uv;

    }
    
    public static float train_ij_uv(Integer Size_Ui_v, List<Integer> Ui_v, Integer u, Integer i, Integer j){
        float loss_ij_uv = 0;
        // --- randomly sample an item $uk$
		int t4 = (int) Math.floor(Math.random()*Size_Ui_v);
		int v = Ui_v.get(t4);                                                // === v
        // ---calculate loss_
        float r_ui = biasU[u] + biasV[i];
        float r_vi = biasU[v] + biasV[i];
        float r_uj = biasU[u] + biasV[j];
        
        for (int f=0; f<d; f++)
        {
            r_ui += U[u][f] * V[i][f];
            r_vi += U[v][f] * V[i][f];
            r_uj += U[u][f] * V[j][f];
        }
        float r_Xiuv = r_ui - r_vi;
        float r_Xuij = r_ui - r_uj;

        float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		float loss_uij =  1f / (1f + (float) Math.pow(Math.E, r_Xuij) );
        // ---------------------------------------------------------------------
        // --- update $U_{u\cdot}$
        for(int f=0; f<d; f++)
        {
            U[u][f] += gamma * ( loss_uij * (V[i][f] - V[j][f]) + loss_iuv * V[i][f]  - alpha_u * U[u][f] );
        }
        // --- update $U_{v\cdot}$
        for(int f=0; f<d; f++)
        {
            U[v][f] += gamma * ( loss_iuv * (-1) * V[i][f]  - alpha_u * U[v][f] );
        }
        // ---------------------------------------------------
        // --- update $V_{i\cdot}$
        for (int f=0; f<d; f++)
        {
            V[i][f] += gamma * ( loss_uij * U[u][f] + loss_iuv * (U[u][f] - U[v][f]) - alpha_v * V[i][f] );
        }
        // ---------------------------------------------------
        // --- update $b_u$
        biasU[u] += gamma * ( loss_iuv  - beta_v * biasU[u] );
        // --- update $b_c$
        biasU[v] += gamma * ( loss_iuv * (-1) - beta_v * biasU[v] );
        // --- update $b_i$
        biasV[i] += gamma * ( loss_uij - beta_v * biasV[i] );
        // --- update $b_j$
        biasV[j] += gamma * ( loss_uij * (-1) - beta_v * biasV[j] );
        // ---------------------------------------------------
        float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuij))) + (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[v])+norm(V[i])+norm(V[j])+biasU[u]*biasU[u]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[j]*biasV[j]));
		loss_ij_uv = bpr_loss - reg_loss;

		return loss_ij_uv;

    }
    
	public static float train_ij_ulv(Integer Size_Ui_l, List<Integer> Ui_l, Integer Size_Ui_v, List<Integer> Ui_v, Integer u, Integer i, Integer j){
		float loss_ij_ulv = 0;
		// --- randomly sample an user $l$
		int t3 = (int) Math.floor(Math.random()*Size_Ui_l);
		int l = Ui_l.get(t3);                                                  // === l
		// --- randomly sample an item $uk$
		int t4 = (int) Math.floor(Math.random()*Size_Ui_v);
		int v = Ui_v.get(t4);                                                  // === v

		// ------------------------------------------------------------------------------------------------
		// --- calculate loss_
		float r_ui = biasU[u] + biasV[i];
		float r_li = biasU[l] + biasV[i];
		float r_vi = biasU[v] + biasV[i];
		float r_uj = biasU[u] + biasV[j];
		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_li += U[l][f] * V[i][f];
			r_vi += U[v][f] * V[i][f];
			r_uj += U[u][f] * V[j][f];
		}
		float r_Xiuv = r_ui - r_vi;
		float r_Xilv = r_li - r_vi;
		float r_Xuij = r_ui - r_uj;

		float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		float loss_ilv =  1f / (1f + (float) Math.pow(Math.E, r_Xilv) );
		float loss_uij =  1f / (1f + (float) Math.pow(Math.E, r_Xuij) );

		// --------------------------------------------------------------------------
		// --- update $V_{i\cdot}$
		for(int f=0; f<d; f++)
		{
			V[i][f] +=  gamma * ( loss_iuv * (U[u][f] - U[v][f])  + loss_ilv * (U[l][f] - U[v][f]) + loss_uij * U[u][f] - alpha_v * V[i][f] );
		}
		// --- update $V_{j\cdot}$
		for(int f=0; f<d; f++)
		{
			V[j][f] +=  gamma * ( loss_uij * (-1) * U[u][f] - alpha_v * V[j][f] );
		}
		// ---------------------------------------------------
		// --- update $U_{u\cdot}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( loss_iuv * V[i][f] + loss_uij * (V[i][f]-V[j][f])- alpha_u * U[u][f] );
		}
		// --- update $U_{l\cdot}$
		for(int f=0; f<d; f++)
		{
			U[l][f] += gamma * ( loss_ilv * V[i][f] - alpha_u * U[l][f] );
		}
		// --- update $U_{c\cdot}$
		for(int f=0; f<d; f++)
		{
			U[v][f] += gamma * ( -1 * (loss_iuv + loss_ilv) * V[i][f] - alpha_u * U[v][f] );
		}
		// ---------------------------------------------------
		// --- update $b_u$
		biasU[u] += gamma * ( loss_iuv - beta_v * biasU[u] );
		// --- update $b_l$
		biasU[l] += gamma * ( loss_ilv - beta_v * biasU[l] );
		// --- update $b_v$
		biasU[v] += gamma * ( -1 * (loss_ilv + loss_iuv) - beta_v * biasU[v] );
		// --- update $b_i$
		biasV[i] += gamma * ( loss_uij - beta_v * biasV[i] );
		// --- update $b_j$
		biasV[j] += gamma * ( loss_uij * (-1) - beta_v * biasV[j] );
		// ---------------------------------------------------
		float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuij))) + (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv))) + (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xilv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[l])+norm(U[v])+norm(V[i])+norm(V[j])+biasU[u]*biasU[u]+biasU[l]*biasU[l]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[j]*biasV[j]));
		loss_ij_ulv = bpr_loss - reg_loss;

		return loss_ij_ulv;
		// ===================================================
	}
	
	public static float train_ic_uv(Integer Size_Iu_uk, List<Integer> Iu_uk, Integer u, Integer i, Integer v){
		float loss_ic_uv = 0;
		// --- randomly sample an item $c$
		int t2 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int c = Iu_uk.get(t2);                                                 // === c   
		// --- calculate loss_
		float r_ui = biasU[u] + biasV[i];
		float r_uc = biasU[u] + biasV[c];
		float r_vi = biasU[v] + biasV[i];

		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_uc += U[u][f] * V[c][f];
			r_vi += U[v][f] * V[i][f];
		}
		float r_Xuic = r_ui - r_uc;
		float r_Xiuv = r_ui - r_vi;

		float loss_uic =  1f / (1f + (float) Math.pow(Math.E, r_Xuic) );
		float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		// --------------------------------------------------------------------------
		// --- update $U_{u}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( loss_uic * (V[i][f] - V[c][f]) + loss_iuv * V[i][f]- alpha_u * U[u][f] );
		}
		// --- update $U_{v}$
		for(int f=0; f<d; f++)
		{
			U[v][f] += gamma * ( (-1) * loss_iuv * V[i][f]- alpha_u * U[v][f] );
		}
		// --- update $V_{i}$
		for (int f=0; f<d; f++)
		{
			V[i][f] += gamma * ( (loss_uic) * U[u][f] + loss_iuv * (U[u][f]-U[v][f]) - alpha_v * V[i][f] );
		}
		// --- update $V_{c}$
		for (int f=0; f<d; f++)
		{
			V[c][f] += gamma * ( (loss_uic) * U[u][f] * (-1) - alpha_v * V[c][f] );
		}
		// --- update $b_i$
		biasV[i] += gamma * ( loss_uic - beta_v * biasV[i] );
		// --- update $b_c$
		biasV[c] += gamma * ( (loss_uic) * (-1) - beta_v * biasV[c] );
		// --- update $b_u$
		biasU[u] += gamma * ( loss_iuv - beta_v * biasU[u] );
		// --- update $b_v$
		biasU[v] += gamma * ( loss_iuv * (-1) - beta_v * biasU[v] );

		// ---------------------------------------------------
		float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuic))) + (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[v])+norm(V[i])+norm(V[c])+biasU[u]*biasU[u]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[c]*biasV[c]));
		loss_ic_uv = bpr_loss - reg_loss;

		return loss_ic_uv;
	}

	public static float train_ipc_uv(Integer Size_Iu_p, List<Integer> Iu_p, Integer Size_Iu_uk, List<Integer> Iu_uk, Integer u, Integer i, Integer v){
		float loss_ipc_uv = 0;
		// --- randomly sample an item $p$
		int t1 = (int) Math.floor(Math.random()*Size_Iu_p);
		int p = Iu_p.get(t1);                                                  // === p
		// --- randomly sample an item $c$
		int t2 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int c = Iu_uk.get(t2);                                                 // === c      
		// --- calculate loss_
		float r_ui = biasU[u] + biasV[i];
		float r_up = biasU[u] + biasV[p];
		float r_uc = biasU[u] + biasV[c];
		float r_vi = biasU[v] + biasV[i];

		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_up += U[u][f] * V[p][f];
			r_uc += U[u][f] * V[c][f];
			r_vi += U[v][f] * V[i][f];
		}
		float r_Xuic = r_ui - r_uc;
		float r_Xupc = r_up - r_uc;
		float r_Xiuv = r_ui - r_vi;

		float loss_uic =  1f / (1f + (float) Math.pow(Math.E, r_Xuic) );
		float loss_upc =  1f / (1f + (float) Math.pow(Math.E, r_Xupc) );
		float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		
		// --------------------------------------------------------------------------
		// --- update $U_{u}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( loss_uic * (V[i][f] - V[c][f]) + loss_upc * (V[p][f] - V[c][f]) + loss_iuv * V[i][f]- alpha_u * U[u][f] );
		}
		// --- update $U_{v}$
		for(int f=0; f<d; f++)
		{
			U[v][f] += gamma * ( (-1) * loss_iuv * V[i][f]- alpha_u * U[v][f] );
		}
		// --- update $V_{i}$
		for (int f=0; f<d; f++)
		{
			V[i][f] += gamma * ( (loss_uic) * U[u][f] + loss_iuv * (U[u][f]-U[v][f]) - alpha_v * V[i][f] );
		}
		// --- update $V_{p}$
		for (int f=0; f<d; f++)
		{
			V[p][f] += gamma * ( (loss_upc) * U[u][f] - alpha_v * V[p][f] );
		}
		// --- update $V_{c}$
		for (int f=0; f<d; f++)
		{
			V[c][f] += gamma * ( (loss_uic + loss_upc) * U[u][f] * (-1) - alpha_v * V[c][f] );
		}
		// --- update $b_i$
		biasV[i] += gamma * ( loss_uic - beta_v * biasV[i] );
		// --- update $b_p$
		biasV[p] += gamma * ( loss_upc - beta_v * biasV[p] );
		// --- update $b_c$
		biasV[c] += gamma * ( (loss_uic + loss_upc) * (-1) - beta_v * biasV[c] );
		// --- update $b_u$
		biasU[u] += gamma * ( loss_iuv - beta_v * biasU[u] );
		// --- update $b_v$
		biasU[v] += gamma * ( loss_iuv * (-1) - beta_v * biasU[v] );

		// ---------------------------------------------------
		float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuic))) + (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xupc))) + (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[v])+norm(V[i])+norm(V[p])+norm(V[c])+biasU[u]*biasU[u]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[p]*biasV[p]+biasV[c]*biasV[c]));
		loss_ipc_uv = bpr_loss - reg_loss;

		return loss_ipc_uv;
	}

	public static float train_icj_uv(Integer Size_Iu_uk, List<Integer> Iu_uk, Integer Size_Iu_j, List<Integer> Iu_j, Integer u, Integer i, Integer v){
		float loss_icj_uv = 0;
		// --- randomly sample an item $c$
		int t2 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int c = Iu_uk.get(t2);                                                 // === c        
		// --- randomly sample an item $j$
		int t3 = (int) Math.floor(Math.random()*Size_Iu_j);
		int j = Iu_j.get(t3);                                                  // === j 
		// --- calculate loss_
		float r_ui = biasU[u] + biasV[i];
		float r_uc = biasU[u] + biasV[c];
		float r_uj = biasU[u] + biasV[j];
		float r_vi = biasU[v] + biasV[i];
		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_uc += U[u][f] * V[c][f];
			r_uj += U[u][f] * V[j][f];
			r_vi += U[v][f] * V[i][f];
		}
		float r_Xuic = r_ui - r_uc;
		float r_Xuij = r_ui - r_uj;
		float r_Xiuv = r_ui - r_vi;

		float loss_uic =  1f / (1f + (float) Math.pow(Math.E, r_Xuic) );
		float loss_uij =  1f / (1f + (float) Math.pow(Math.E, r_Xuij) );
		float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		// --------------------------------------------------------------------------
		// --- update $U_{u}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( loss_uic * (V[i][f] - V[c][f]) + loss_uij * (V[i][f] - V[j][f]) + loss_iuv * V[i][f]- alpha_u * U[u][f] );
		}
		// --- update $U_{v}$
		for(int f=0; f<d; f++)
		{
			U[v][f] += gamma * ( (-1) * loss_iuv * V[i][f]- alpha_u * U[v][f] );
		}
		// --- update $V_{i}$
		for (int f=0; f<d; f++)
		{
			V[i][f] += gamma * ( (loss_uic + loss_uij) * U[u][f] + loss_iuv * (U[u][f]-U[v][f]) - alpha_v * V[i][f] );
		}
		// --- update $V_{c}$
		for (int f=0; f<d; f++)
		{
			V[c][f] += gamma * ( (loss_uic) * U[u][f] * (-1) - alpha_v * V[c][f] );
		}
		// --- update $V_{j}$
		for (int f=0; f<d; f++)
		{
			V[j][f] += gamma * ( (loss_uij) * U[u][f] * (-1) - alpha_v * V[j][f] );
		}
		// --- update $b_i$
		biasV[i] += gamma * ( loss_uic + loss_uij - beta_v * biasV[i] );
		// --- update $b_c$
		biasV[c] += gamma * ( (loss_uic) * (-1) - beta_v * biasV[c] );
		// --- update $b_j$
		biasV[j] += gamma * ( (loss_uij) * (-1) - beta_v * biasV[j] );
		// --- update $b_u$
		biasU[u] += gamma * ( loss_iuv - beta_v * biasU[u] );
		// --- update $b_v$
		biasU[v] += gamma * ( loss_iuv * (-1) - beta_v * biasU[v] );
		
		// ---------------------------------------------------
		float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuic))) + (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuij)))+(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[v])+norm(V[i])+norm(V[c])+norm(V[j])+biasU[u]*biasU[u]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[c]*biasV[c]+biasV[j]*biasV[j]));
		loss_icj_uv = bpr_loss - reg_loss;

		return loss_icj_uv;
	}

	public static float train_ipcj_uv(Integer Size_Iu_p, List<Integer> Iu_p, Integer Size_Iu_uk, List<Integer> Iu_uk, Integer Size_Iu_j, List<Integer> Iu_j, Integer u, Integer i, Integer v){
		float loss_ipcj_uv = 0;
		// --- randomly sample an item $p$
		int t1 = (int) Math.floor(Math.random()*Size_Iu_p);
		int p = Iu_p.get(t1);                                                  // === p
		// --- randomly sample an item $c$
		int t2 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int c = Iu_uk.get(t2);                                                 // === c        
		// --- randomly sample an item $j$
		int t3 = (int) Math.floor(Math.random()*Size_Iu_j);
		int j = Iu_j.get(t3);                                                  // === j 
		// --- calculate loss_
		float r_ui = biasU[u] + biasV[i];
		float r_up = biasU[u] + biasV[p];
		float r_uc = biasU[u] + biasV[c];
		float r_uj = biasU[u] + biasV[j];
		float r_vi = biasU[v] + biasV[i];
		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_up += U[u][f] * V[p][f];
			r_uc += U[u][f] * V[c][f];
			r_uj += U[u][f] * V[j][f];
			r_vi += U[v][f] * V[i][f];
		}
		float r_Xuic = r_ui - r_uc;
		float r_Xuij = r_ui - r_uj;
		float r_Xupc = r_up - r_uc;
		float r_Xupj = r_up - r_uj;
		float r_Xiuv = r_ui - r_vi;

		float loss_uic =  1f / (1f + (float) Math.pow(Math.E, r_Xuic) );
		float loss_uij =  1f / (1f + (float) Math.pow(Math.E, r_Xuij) );
		float loss_upc =  1f / (1f + (float) Math.pow(Math.E, r_Xupc) );
		float loss_upj =  1f / (1f + (float) Math.pow(Math.E, r_Xupj) );
		float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		
		// --------------------------------------------------------------------------
		// --- update $U_{u}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( loss_uic * (V[i][f] - V[c][f]) + loss_uij * (V[i][f] - V[j][f])  + loss_upc * (V[p][f] - V[c][f]) + loss_upj * (V[p][f] - V[j][f]) + loss_iuv * V[i][f]- alpha_u * U[u][f] );
		}
		// --- update $U_{v}$
		for(int f=0; f<d; f++)
		{
			U[v][f] += gamma * ( (-1) * loss_iuv * V[i][f]- alpha_u * U[v][f] );
		}
		// --- update $V_{i}$
		for (int f=0; f<d; f++)
		{
			V[i][f] += gamma * ( (loss_uic + loss_uij) * U[u][f] + loss_iuv * (U[u][f]-U[v][f]) - alpha_v * V[i][f] );
		}
		// --- update $V_{p}$
		for (int f=0; f<d; f++)
		{
			V[p][f] += gamma * ( (loss_upc + loss_upj) * U[u][f] - alpha_v * V[p][f] );
		}
		// --- update $V_{c}$
		for (int f=0; f<d; f++)
		{
			V[c][f] += gamma * ( (loss_uic + loss_upc) * U[u][f] * (-1) - alpha_v * V[c][f] );
		}
		// --- update $V_{j}$
		for (int f=0; f<d; f++)
		{
			V[j][f] += gamma * ( (loss_uij + loss_upj) * U[u][f] * (-1) - alpha_v * V[j][f] );
		}
		// --- update $b_i$
		biasV[i] += gamma * ( loss_uic + loss_uij - beta_v * biasV[i] );
		// --- update $b_p$
		biasV[p] += gamma * ( loss_upc + loss_upj - beta_v * biasV[p] );
		// --- update $b_c$
		biasV[c] += gamma * ( (loss_uic + loss_upc) * (-1) - beta_v * biasV[c] );
		// --- update $b_j$
		biasV[j] += gamma * ( (loss_uij + loss_upj) * (-1) - beta_v * biasV[j] );

		// --- update $b_u$
		biasU[u] += gamma * ( loss_iuv - beta_v * biasU[u] );
		// --- update $b_v$
		biasU[v] += gamma * ( loss_iuv * (-1) - beta_v * biasU[v] );
		
		// ---------------------------------------------------
		float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuic))) + (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuij)))+ (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xupc)))+ (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xupj))) +(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[v])+norm(V[i])+norm(V[p])+norm(V[c])+norm(V[j])+biasU[u]*biasU[u]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[p]*biasV[p]+biasV[c]*biasV[c]+biasV[j]*biasV[j]));
		loss_ipcj_uv = bpr_loss - reg_loss;

		return loss_ipcj_uv;

	}
	// --------------------------- 稍微有点区别 ---------------------------
	public static float train_ic_uv_(Integer Size_Iu_uk, List<Integer> Iu_uk, Integer u, Integer i, Integer Size_Ui_v, List<Integer> Ui_v){
		float loss_ic_uv = 0;
		// --- randomly sample an item $c$
		int t2 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int c = Iu_uk.get(t2);                                                 // === c   
		// --- randomly sample an item $uk$
		int t4 = (int) Math.floor(Math.random()*Size_Ui_v);
		int v = Ui_v.get(t4);                                                  // === v
		// --- calculate loss_
		float r_ui = biasU[u] + biasV[i];
		float r_uc = biasU[u] + biasV[c];
		float r_vi = biasU[v] + biasV[i];

		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_uc += U[u][f] * V[c][f];
			r_vi += U[v][f] * V[i][f];
		}
		float r_Xuic = r_ui - r_uc;
		float r_Xiuv = r_ui - r_vi;

		float loss_uic =  1f / (1f + (float) Math.pow(Math.E, r_Xuic) );
		float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		// --------------------------------------------------------------------------
		// --- update $U_{u}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( lambda*loss_uic * (V[i][f] - V[c][f]) + (1-lambda)*loss_iuv * V[i][f]- alpha_u * U[u][f] );
		}
		// --- update $U_{v}$
		for(int f=0; f<d; f++)
		{
			U[v][f] += gamma * ( (1-lambda)*(-1) * loss_iuv * V[i][f]- alpha_u * U[v][f] );
		}
		// --- update $V_{i}$
		for (int f=0; f<d; f++)
		{
			V[i][f] += gamma * ( lambda*(loss_uic) * U[u][f] + (1-lambda)*loss_iuv * (U[u][f]-U[v][f]) - alpha_v * V[i][f] );
		}
		// --- update $V_{c}$
		for (int f=0; f<d; f++)
		{
			V[c][f] += gamma * ( lambda*(loss_uic) * U[u][f] * (-1) - alpha_v * V[c][f] );
		}
		// --- update $b_i$
		biasV[i] += gamma * ( lambda*loss_uic - beta_v * biasV[i] );
		// --- update $b_c$
		biasV[c] += gamma * ( lambda*(loss_uic) * (-1) - beta_v * biasV[c] );
		// --- update $b_u$
		biasU[u] += gamma * ( (1-lambda)*loss_iuv - beta_v * biasU[u] );
		// --- update $b_v$
		biasU[v] += gamma * ( (1-lambda)*loss_iuv * (-1) - beta_v * biasU[v] );

		// ---------------------------------------------------
		float bpr_loss = lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuic))) + (1-lambda)*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[v])+norm(V[i])+norm(V[c])+biasU[u]*biasU[u]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[c]*biasV[c]));
		loss_ic_uv = bpr_loss - reg_loss;

		return loss_ic_uv;
	}

	public static float train_ipc_uv_(Integer Size_Iu_p, List<Integer> Iu_p, Integer Size_Iu_uk, List<Integer> Iu_uk, Integer u, Integer i, Integer Size_Ui_v, List<Integer> Ui_v){
		float loss_ipc_uv = 0;
		// --- randomly sample an item $p$
		int t1 = (int) Math.floor(Math.random()*Size_Iu_p);
		int p = Iu_p.get(t1);                                                  // === p
		// --- randomly sample an item $c$
		int t2 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int c = Iu_uk.get(t2);                                                 // === c  
		// --- randomly sample an item $uk$
		int t4 = (int) Math.floor(Math.random()*Size_Ui_v);
		int v = Ui_v.get(t4);                                                  // === v    
		// --- calculate loss_
		float r_ui = biasU[u] + biasV[i];
		float r_up = biasU[u] + biasV[p];
		float r_uc = biasU[u] + biasV[c];
		float r_vi = biasU[v] + biasV[i];

		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_up += U[u][f] * V[p][f];
			r_uc += U[u][f] * V[c][f];
			r_vi += U[v][f] * V[i][f];
		}
		float r_Xuic = r_ui - r_uc;
		float r_Xupc = r_up - r_uc;
		float r_Xiuv = r_ui - r_vi;

		float loss_uic =  1f / (1f + (float) Math.pow(Math.E, r_Xuic) );
		float loss_upc =  1f / (1f + (float) Math.pow(Math.E, r_Xupc) );
		float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		
		// --------------------------------------------------------------------------
		// --- update $U_{u}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( lambda*(loss_uic * (V[i][f] - V[c][f]) + loss_upc * (V[p][f] - V[c][f])) + (1-lambda)*loss_iuv * V[i][f]- alpha_u * U[u][f] );
		}
		// --- update $U_{v}$
		for(int f=0; f<d; f++)
		{
			U[v][f] += gamma * ( (1-lambda)*(-1) * loss_iuv * V[i][f]- alpha_u * U[v][f] );
		}
		// --- update $V_{i}$
		for (int f=0; f<d; f++)
		{
			V[i][f] += gamma * ( lambda*(loss_uic) * U[u][f] + (1-lambda)*loss_iuv * (U[u][f]-U[v][f]) - alpha_v * V[i][f] );
		}
		// --- update $V_{p}$
		for (int f=0; f<d; f++)
		{
			V[p][f] += gamma * ( lambda*loss_upc * U[u][f] - alpha_v * V[p][f] );
		}
		// --- update $V_{c}$
		for (int f=0; f<d; f++)
		{
			V[c][f] += gamma * ( (lambda)*(loss_uic + loss_upc) * U[u][f] * (-1) - alpha_v * V[c][f] );
		}
		// --- update $b_i$
		biasV[i] += gamma * ( lambda*loss_uic - beta_v * biasV[i] );
		// --- update $b_p$
		biasV[p] += gamma * ( lambda*loss_upc - beta_v * biasV[p] );
		// --- update $b_c$
		biasV[c] += gamma * ( lambda*(loss_uic + loss_upc) * (-1) - beta_v * biasV[c] );
		// --- update $b_u$
		biasU[u] += gamma * ( (1-lambda)*loss_iuv - beta_v * biasU[u] );
		// --- update $b_v$
		biasU[v] += gamma * ( (1-lambda)*loss_iuv * (-1) - beta_v * biasU[v] );

		// ---------------------------------------------------
		float bpr_loss = lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuic))) + lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xupc))) + (1-lambda)*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[v])+norm(V[i])+norm(V[p])+norm(V[c])+biasU[u]*biasU[u]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[p]*biasV[p]+biasV[c]*biasV[c]));
		loss_ipc_uv = bpr_loss - reg_loss;

		return loss_ipc_uv;
	}

	public static float train_icj_uv_(Integer Size_Iu_uk, List<Integer> Iu_uk, Integer Size_Iu_j, List<Integer> Iu_j, Integer u, Integer i, Integer Size_Ui_v, List<Integer> Ui_v){
		float loss_icj_uv = 0;
		// --- randomly sample an item $c$
		int t2 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int c = Iu_uk.get(t2);                                                 // === c        
		// --- randomly sample an item $j$
		int t3 = (int) Math.floor(Math.random()*Size_Iu_j);
		int j = Iu_j.get(t3);                                                  // === j 
		// --- randomly sample an item $uk$
		int t4 = (int) Math.floor(Math.random()*Size_Ui_v);
		int v = Ui_v.get(t4);                                                  // === v 
		// --- calculate loss_
		float r_ui = biasU[u] + biasV[i];
		float r_uc = biasU[u] + biasV[c];
		float r_uj = biasU[u] + biasV[j];
		float r_vi = biasU[v] + biasV[i];
		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_uc += U[u][f] * V[c][f];
			r_uj += U[u][f] * V[j][f];
			r_vi += U[v][f] * V[i][f];
		}
		float r_Xuic = r_ui - r_uc;
		float r_Xuij = r_ui - r_uj;
		float r_Xiuv = r_ui - r_vi;

		float loss_uic =  1f / (1f + (float) Math.pow(Math.E, r_Xuic) );
		float loss_uij =  1f / (1f + (float) Math.pow(Math.E, r_Xuij) );
		float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		// --------------------------------------------------------------------------
		// --- update $U_{u}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( lambda*(loss_uic * (V[i][f] - V[c][f]) + loss_uij * (V[i][f] - V[j][f])) + (1-lambda)*loss_iuv * V[i][f]- alpha_u * U[u][f] );
		}
		// --- update $U_{v}$
		for(int f=0; f<d; f++)
		{
			U[v][f] += gamma * ( (1-lambda)*(-1) * loss_iuv * V[i][f]- alpha_u * U[v][f] );
		}
		// --- update $V_{i}$
		for (int f=0; f<d; f++)
		{
			V[i][f] += gamma * ( lambda*(loss_uic + loss_uij) * U[u][f] + (1-lambda)*loss_iuv * (U[u][f]-U[v][f]) - alpha_v * V[i][f] );
		}
		// --- update $V_{c}$
		for (int f=0; f<d; f++)
		{
			V[c][f] += gamma * ( lambda*(loss_uic) * U[u][f] * (-1) - alpha_v * V[c][f] );
		}
		// --- update $V_{j}$
		for (int f=0; f<d; f++)
		{
			V[j][f] += gamma * ( lambda*(loss_uij) * U[u][f] * (-1) - alpha_v * V[j][f] );
		}
		// --- update $b_i$
		biasV[i] += gamma * ( lambda*(loss_uic + loss_uij) - beta_v * biasV[i] );
		// --- update $b_c$
		biasV[c] += gamma * ( lambda*(loss_uic) * (-1) - beta_v * biasV[c] );
		// --- update $b_j$
		biasV[j] += gamma * ( lambda*(loss_uij) * (-1) - beta_v * biasV[j] );
		// --- update $b_u$
		biasU[u] += gamma * ( (1-lambda)*loss_iuv - beta_v * biasU[u] );
		// --- update $b_v$
		biasU[v] += gamma * ( (1-lambda)*loss_iuv * (-1) - beta_v * biasU[v] );
		
		// ---------------------------------------------------
		float bpr_loss = lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuic))) + lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuij)))+(1-lambda)*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[v])+norm(V[i])+norm(V[c])+norm(V[j])+biasU[u]*biasU[u]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[c]*biasV[c]+biasV[j]*biasV[j]));
		loss_icj_uv = bpr_loss - reg_loss;

		return loss_icj_uv;
	}

	public static float train_ipcj_uv_(Integer Size_Iu_p, List<Integer> Iu_p, Integer Size_Iu_uk, List<Integer> Iu_uk, Integer Size_Iu_j, List<Integer> Iu_j, Integer u, Integer i, Integer Size_Ui_v, List<Integer> Ui_v){
		float loss_ipcj_uv = 0;
		// --- randomly sample an item $p$
		int t1 = (int) Math.floor(Math.random()*Size_Iu_p);
		int p = Iu_p.get(t1);                                                  // === p
		// --- randomly sample an item $c$
		int t2 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int c = Iu_uk.get(t2);                                                 // === c        
		// --- randomly sample an item $j$
		int t3 = (int) Math.floor(Math.random()*Size_Iu_j);
		int j = Iu_j.get(t3);                                                  // === j 
		// --- randomly sample an item $uk$
		int t4 = (int) Math.floor(Math.random()*Size_Ui_v);
		int v = Ui_v.get(t4);                                                  // === v 
		// --- calculate loss_
		float r_ui = biasU[u] + biasV[i];
		float r_up = biasU[u] + biasV[p];
		float r_uc = biasU[u] + biasV[c];
		float r_uj = biasU[u] + biasV[j];
		float r_vi = biasU[v] + biasV[i];
		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_up += U[u][f] * V[p][f];
			r_uc += U[u][f] * V[c][f];
			r_uj += U[u][f] * V[j][f];
			r_vi += U[v][f] * V[i][f];
		}
		float r_Xuic = r_ui - r_uc;
		float r_Xuij = r_ui - r_uj;
		float r_Xupc = r_up - r_uc;
		float r_Xupj = r_up - r_uj;
		float r_Xiuv = r_ui - r_vi;

		float loss_uic =  1f / (1f + (float) Math.pow(Math.E, r_Xuic) );
		float loss_uij =  1f / (1f + (float) Math.pow(Math.E, r_Xuij) );
		float loss_upc =  1f / (1f + (float) Math.pow(Math.E, r_Xupc) );
		float loss_upj =  1f / (1f + (float) Math.pow(Math.E, r_Xupj) );
		float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		
		// --------------------------------------------------------------------------
		// --- update $U_{u}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( lambda*(loss_uic * (V[i][f] - V[c][f]) + loss_uij * (V[i][f] - V[j][f])  + loss_upc * (V[p][f] - V[c][f]) + loss_upj * (V[p][f] - V[j][f])) + (1-lambda)*loss_iuv * V[i][f]- alpha_u * U[u][f] );
		}
		// --- update $U_{v}$
		for(int f=0; f<d; f++)
		{
			U[v][f] += gamma * ( (1-lambda)*(-1) * loss_iuv * V[i][f]- alpha_u * U[v][f] );
		}
		// --- update $V_{i}$
		for (int f=0; f<d; f++)
		{
			V[i][f] += gamma * ( lambda*(loss_uic + loss_uij) * U[u][f] + (1-lambda)*loss_iuv * (U[u][f]-U[v][f]) - alpha_v * V[i][f] );
		}
		// --- update $V_{p}$
		for (int f=0; f<d; f++)
		{
			V[p][f] += gamma * ( lambda*(loss_upc + loss_upj) * U[u][f] - alpha_v * V[p][f] );
		}
		// --- update $V_{c}$
		for (int f=0; f<d; f++)
		{
			V[c][f] += gamma * ( lambda*(loss_uic + loss_upc) * U[u][f] * (-1) - alpha_v * V[c][f] );
		}
		// --- update $V_{j}$
		for (int f=0; f<d; f++)
		{
			V[j][f] += gamma * ( lambda*(loss_uij + loss_upj) * U[u][f] * (-1) - alpha_v * V[j][f] );
		}
		// --- update $b_i$
		biasV[i] += gamma * ( lambda*(loss_uic + loss_uij) - beta_v * biasV[i] );
		// --- update $b_p$
		biasV[p] += gamma * ( lambda*(loss_upc + loss_upj) - beta_v * biasV[p] );
		// --- update $b_c$
		biasV[c] += gamma * ( lambda*(loss_uic + loss_upc) * (-1) - beta_v * biasV[c] );
		// --- update $b_j$
		biasV[j] += gamma * ( lambda*(loss_uij + loss_upj) * (-1) - beta_v * biasV[j] );

		// --- update $b_u$
		biasU[u] += gamma * ( (1-lambda)*loss_iuv - beta_v * biasU[u] );
		// --- update $b_v$
		biasU[v] += gamma * ( (1-lambda)*loss_iuv * (-1) - beta_v * biasU[v] );
		
		// ---------------------------------------------------
		float bpr_loss = lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuic))) + lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuij)))+ lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xupc)))+ lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xupj))) +(1-lambda)*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[v])+norm(V[i])+norm(V[p])+norm(V[c])+norm(V[j])+biasU[u]*biasU[u]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[p]*biasV[p]+biasV[c]*biasV[c]+biasV[j]*biasV[j]));
		loss_ipcj_uv = bpr_loss - reg_loss;

		return loss_ipcj_uv;

	}
	// -------------------------------------------------------------------
	public static float train_ic_ulv(Integer Size_Iu_uk, List<Integer> Iu_uk, Integer u, Integer i, Integer Size_Ui_l, List<Integer> Ui_l, Integer Size_Ui_v, List<Integer> Ui_v){
		float loss_ic_ulv = 0;
		// --- randomly sample an item $c$
		int t2 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int c = Iu_uk.get(t2);                                                 // === c        
		// --- randomly sample an user $l$
		int t4 = (int) Math.floor(Math.random()*Size_Ui_l);
		int l = Ui_l.get(t4);                                                  // === l
		// --- randomly sample an item $uk$
		int t5 = (int) Math.floor(Math.random()*Size_Ui_v);
		int v = Ui_v.get(t5);                                                  // === v

		// --- calculate loss_
		float r_ui = biasU[u] + biasV[i];
		float r_uc = biasU[u] + biasV[c];
		float r_li = biasU[l] + biasV[i];
		float r_vi = biasU[v] + biasV[i];
		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_uc += U[u][f] * V[c][f];
			r_li += U[l][f] * V[i][f];
			r_vi += U[v][f] * V[i][f];
		}
		float r_Xuic = r_ui - r_uc;
		float r_Xiuv = r_ui - r_vi;
		float r_Xilv = r_li - r_vi;

		float loss_uic =  1f / (1f + (float) Math.pow(Math.E, r_Xuic) );
		float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		float loss_ilv =  1f / (1f + (float) Math.pow(Math.E, r_Xilv) );
		
		// --------------------------------------------------------------------------
		// --- update $U_{u}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( lambda*loss_uic * (V[i][f] - V[c][f]) + (1-lambda)*loss_iuv * V[i][f]- alpha_u * U[u][f] );
		}
		// --- update $U_{l}$
		for(int f=0; f<d; f++)
		{
			U[l][f] += gamma * ( (1-lambda)*loss_ilv * V[i][f] - alpha_u * U[l][f] );
		}
		// --- update $U_{v}$
		for(int f=0; f<d; f++)
		{
			U[v][f] += gamma * ( (1-lambda)*(-1) * (loss_iuv + loss_ilv) * V[i][f] - alpha_u * U[v][f] );
		}
		// --- update $V_{i}$
		for (int f=0; f<d; f++)
		{
			V[i][f] += gamma * ( lambda*(loss_uic) * U[u][f] + (1-lambda)*(loss_iuv * (U[u][f]-U[v][f]) + loss_ilv * (U[l][f]-U[v][f])) - alpha_v * V[i][f] );
		}
		// --- update $V_{c}$
		for (int f=0; f<d; f++)
		{
			V[c][f] += gamma * ( lambda*(loss_uic) * U[u][f] * (-1) - alpha_v * V[c][f] );
		}
		// --- update $b_i$
		biasV[i] += gamma * ( lambda*loss_uic - beta_v * biasV[i] );
		// --- update $b_c$
		biasV[c] += gamma * ( lambda*(loss_uic) * (-1) - beta_v * biasV[c] );

		// --- update $b_u$
		biasU[u] += gamma * ( (1-lambda)*loss_iuv - beta_v * biasU[u] );
		// --- update $b_l$
		biasU[l] += gamma * ( (1-lambda)*loss_ilv - beta_v * biasU[l] );
		// --- update $b_v$
		biasU[v] += gamma * ( (1-lambda)*loss_iuv * (-1) - beta_v * biasU[v] );
		
		// ---------------------------------------------------
		float bpr_loss = lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuic))) +(1-lambda)*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xilv)))+(1-lambda)*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[l])+norm(U[v])+norm(V[i])+norm(V[c])+biasU[u]*biasU[u]+biasU[l]*biasU[l]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[c]*biasV[c]));
		loss_ic_ulv = bpr_loss - reg_loss;

		return loss_ic_ulv;
	}

	public static float train_ipc_ulv(Integer Size_Iu_p, List<Integer> Iu_p, Integer Size_Iu_uk, List<Integer> Iu_uk, Integer u, Integer i, Integer Size_Ui_l, List<Integer> Ui_l,Integer Size_Ui_v, List<Integer> Ui_v){
		float loss_ipc_ulv = 0;
		// --- randomly sample an item $p$
		int t1 = (int) Math.floor(Math.random()*Size_Iu_p);
		int p = Iu_p.get(t1);                                                  // === p
		// --- randomly sample an item $c$
		int t2 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int c = Iu_uk.get(t2);                                                 // === c        
		// --- randomly sample an user $l$
		int t4 = (int) Math.floor(Math.random()*Size_Ui_l);
		int l = Ui_l.get(t4);                                                  // === l
		// --- randomly sample an item $uk$
		int t5 = (int) Math.floor(Math.random()*Size_Ui_v);
		int v = Ui_v.get(t5);                                                  // === v

		// --- calculate loss_
		float r_ui = biasU[u] + biasV[i];
		float r_up = biasU[u] + biasV[p];
		float r_uc = biasU[u] + biasV[c];
		float r_li = biasU[l] + biasV[i];
		float r_vi = biasU[v] + biasV[i];
		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_up += U[u][f] * V[p][f];
			r_uc += U[u][f] * V[c][f];
			r_li += U[l][f] * V[i][f];
			r_vi += U[v][f] * V[i][f];
		}
		float r_Xuic = r_ui - r_uc;
		float r_Xupc = r_up - r_uc;
		float r_Xiuv = r_ui - r_vi;
		float r_Xilv = r_li - r_vi;

		float loss_uic =  1f / (1f + (float) Math.pow(Math.E, r_Xuic) );
		float loss_upc =  1f / (1f + (float) Math.pow(Math.E, r_Xupc) );
		float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		float loss_ilv =  1f / (1f + (float) Math.pow(Math.E, r_Xilv) );
		
		// --------------------------------------------------------------------------
		// --- update $U_{u}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( lambda * (loss_uic * (V[i][f] - V[c][f]) + loss_upc * (V[p][f] - V[c][f])) + (1-lambda)*loss_iuv * V[i][f]- alpha_u * U[u][f] );
		}
		// --- update $U_{l}$
		for(int f=0; f<d; f++)
		{
			U[l][f] += gamma * ( (1-lambda)*loss_ilv * V[i][f] - alpha_u * U[l][f] );
		}
		// --- update $U_{v}$
		for(int f=0; f<d; f++)
		{
			U[v][f] += gamma * ( (1-lambda)*(-1) * (loss_iuv + loss_ilv) * V[i][f] - alpha_u * U[v][f] );
		}
		// --- update $V_{i}$
		for (int f=0; f<d; f++)
		{
			V[i][f] += gamma * ( lambda*(loss_uic) * U[u][f] + (1-lambda)*(loss_iuv * (U[u][f]-U[v][f]) + loss_ilv * (U[l][f]-U[v][f])) - alpha_v * V[i][f] );
		}
		// --- update $V_{p}$
		for (int f=0; f<d; f++)
		{
			V[p][f] += gamma * ( lambda*(loss_upc) * U[u][f] - alpha_v * V[p][f] );
		}
		// --- update $V_{c}$
		for (int f=0; f<d; f++)
		{
			V[c][f] += gamma * ( lambda*(loss_uic + loss_upc) * U[u][f] * (-1) - alpha_v * V[c][f] );
		}
		// --- update $b_i$
		biasV[i] += gamma * ( lambda*loss_uic - beta_v * biasV[i] );
		// --- update $b_p$
		biasV[p] += gamma * ( lambda*loss_upc - beta_v * biasV[p] );
		// --- update $b_c$
		biasV[c] += gamma * ( lambda*(loss_uic + loss_upc) * (-1) - beta_v * biasV[c] );

		// --- update $b_u$
		biasU[u] += gamma * ( (1-lambda)*loss_iuv - beta_v * biasU[u] );
		// --- update $b_l$
		biasU[l] += gamma * ( (1-lambda)*loss_ilv - beta_v * biasU[l] );
		// --- update $b_v$
		biasU[v] += gamma * ( (1-lambda)*loss_iuv * (-1) - beta_v * biasU[v] );
		
		// ---------------------------------------------------
		float bpr_loss = lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuic))) + lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xupc))) +(1-lambda)*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xilv)))+(1-lambda)*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[l])+norm(U[v])+norm(V[i])+norm(V[p])+norm(V[c])+biasU[u]*biasU[u]+biasU[l]*biasU[l]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[p]*biasV[p]+biasV[c]*biasV[c]));
		loss_ipc_ulv = bpr_loss - reg_loss;

		return loss_ipc_ulv;

	}
	
	public static float train_icj_ulv(Integer Size_Iu_uk, List<Integer> Iu_uk, Integer Size_Iu_j, List<Integer> Iu_j, Integer u, Integer i, Integer Size_Ui_l, List<Integer> Ui_l,Integer Size_Ui_v, List<Integer> Ui_v){
		float loss_icj_ulv = 0;
		// --- randomly sample an item $c$
		int t2 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int c = Iu_uk.get(t2);                                                 // === c        
		// --- randomly sample an item $j$
		int t3 = (int) Math.floor(Math.random()*Size_Iu_j);
		int j = Iu_j.get(t3);                                                  // === j 
		// --- randomly sample an user $l$
		int t4 = (int) Math.floor(Math.random()*Size_Ui_l);
		int l = Ui_l.get(t4);                                                  // === l
		// --- randomly sample an item $uk$
		int t5 = (int) Math.floor(Math.random()*Size_Ui_v);
		int v = Ui_v.get(t5);                                                  // === v

		// --- calculate loss_
		float r_ui = biasU[u] + biasV[i];
		float r_uc = biasU[u] + biasV[c];
		float r_uj = biasU[u] + biasV[j];
		float r_li = biasU[l] + biasV[i];
		float r_vi = biasU[v] + biasV[i];
		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_uc += U[u][f] * V[c][f];
			r_uj += U[u][f] * V[j][f];
			r_li += U[l][f] * V[i][f];
			r_vi += U[v][f] * V[i][f];
		}
		float r_Xuic = r_ui - r_uc;
		float r_Xuij = r_ui - r_uj;
		float r_Xiuv = r_ui - r_vi;
		float r_Xilv = r_li - r_vi;

		float loss_uic =  1f / (1f + (float) Math.pow(Math.E, r_Xuic) );
		float loss_uij =  1f / (1f + (float) Math.pow(Math.E, r_Xuij) );
		float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		float loss_ilv =  1f / (1f + (float) Math.pow(Math.E, r_Xilv) );
		
		// --------------------------------------------------------------------------
		// --- update $U_{u}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( lambda*(loss_uic * (V[i][f] - V[c][f]) + loss_uij * (V[i][f] - V[j][f])) + (1-lambda)*loss_iuv * V[i][f]- alpha_u * U[u][f] );
		}
		// --- update $U_{l}$
		for(int f=0; f<d; f++)
		{
			U[l][f] += gamma * ( (1-lambda)*loss_ilv * V[i][f] - alpha_u * U[l][f] );
		}
		// --- update $U_{v}$
		for(int f=0; f<d; f++)
		{
			U[v][f] += gamma * ( (1-lambda)*(-1) * (loss_iuv + loss_ilv) * V[i][f] - alpha_u * U[v][f] );
		}
		// --- update $V_{i}$
		for (int f=0; f<d; f++)
		{
			V[i][f] += gamma * ( lambda*(loss_uic + loss_uij) * U[u][f] + (1-lambda)*(loss_iuv * (U[u][f]-U[v][f]) + loss_ilv * (U[l][f]-U[v][f])) - alpha_v * V[i][f] );
		}
		// --- update $V_{c}$
		for (int f=0; f<d; f++)
		{
			V[c][f] += gamma * ( lambda*(loss_uic ) * U[u][f] * (-1) - alpha_v * V[c][f] );
		}
		// --- update $V_{j}$
		for (int f=0; f<d; f++)
		{
			V[j][f] += gamma * ( lambda*(loss_uij ) * U[u][f] * (-1) - alpha_v * V[j][f] );
		}
		// --- update $b_i$
		biasV[i] += gamma * ( lambda*(loss_uic + loss_uij) - beta_v * biasV[i] );
		// --- update $b_c$
		biasV[c] += gamma * ( lambda*(loss_uic ) * (-1) - beta_v * biasV[c] );
		// --- update $b_j$
		biasV[j] += gamma * ( lambda*(loss_uij ) * (-1) - beta_v * biasV[j] );

		// --- update $b_u$
		biasU[u] += gamma * ( (1-lambda)*loss_iuv - beta_v * biasU[u] );
		// --- update $b_l$
		biasU[l] += gamma * ( (1-lambda)*loss_ilv - beta_v * biasU[l] );
		// --- update $b_v$
		biasU[v] += gamma * ( (1-lambda)*loss_iuv * (-1) - beta_v * biasU[v] );
		
		// ---------------------------------------------------
		float bpr_loss = lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuic))) + lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuij))) +(1-lambda)*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xilv)))+(1-lambda)*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[l])+norm(U[v])+norm(V[i])+norm(V[c])+norm(V[j])+biasU[u]*biasU[u]+biasU[l]*biasU[l]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[c]*biasV[c]+biasV[j]*biasV[j]));
		loss_icj_ulv = bpr_loss - reg_loss;

		return loss_icj_ulv;

	}
	
	public static float train_ipcj_ulv(Integer Size_Iu_p, List<Integer> Iu_p, Integer Size_Iu_uk, List<Integer> Iu_uk, Integer Size_Iu_j, List<Integer> Iu_j, Integer u, Integer i, Integer Size_Ui_l, List<Integer> Ui_l,Integer Size_Ui_v, List<Integer> Ui_v){
		float loss_ipcj_ulv = 0;
		// --- randomly sample an item $p$
		int t1 = (int) Math.floor(Math.random()*Size_Iu_p);
		int p = Iu_p.get(t1);                                                  // === p
		// --- randomly sample an item $c$
		int t2 = (int) Math.floor(Math.random()*Size_Iu_uk);
		int c = Iu_uk.get(t2);                                                 // === c        
		// --- randomly sample an item $j$
		int t3 = (int) Math.floor(Math.random()*Size_Iu_j);
		int j = Iu_j.get(t3);                                                  // === j 
		// --- randomly sample an user $l$
		int t4 = (int) Math.floor(Math.random()*Size_Ui_l);
		int l = Ui_l.get(t4);                                                  // === l
		// --- randomly sample an item $uk$
		int t5 = (int) Math.floor(Math.random()*Size_Ui_v);
		int v = Ui_v.get(t5);                                                  // === v

		// --- calculate loss_
		float r_ui = biasU[u] + biasV[i];
		float r_up = biasU[u] + biasV[p];
		float r_uc = biasU[u] + biasV[c];
		float r_uj = biasU[u] + biasV[j];
		float r_li = biasU[l] + biasV[i];
		float r_vi = biasU[v] + biasV[i];
		for (int f=0; f<d; f++)
		{
			r_ui += U[u][f] * V[i][f];
			r_up += U[u][f] * V[p][f];
			r_uc += U[u][f] * V[c][f];
			r_uj += U[u][f] * V[j][f];
			r_li += U[l][f] * V[i][f];
			r_vi += U[v][f] * V[i][f];
		}
		float r_Xuic = r_ui - r_uc;
		float r_Xuij = r_ui - r_uj;
		float r_Xupc = r_up - r_uc;
		float r_Xupj = r_up - r_uj;
		float r_Xiuv = r_ui - r_vi;
		float r_Xilv = r_li - r_vi;

		float loss_uic =  1f / (1f + (float) Math.pow(Math.E, r_Xuic) );
		float loss_uij =  1f / (1f + (float) Math.pow(Math.E, r_Xuij) );
		float loss_upc =  1f / (1f + (float) Math.pow(Math.E, r_Xupc) );
		float loss_upj =  1f / (1f + (float) Math.pow(Math.E, r_Xupj) );
		float loss_iuv =  1f / (1f + (float) Math.pow(Math.E, r_Xiuv) );
		float loss_ilv =  1f / (1f + (float) Math.pow(Math.E, r_Xilv) );
		
		// --------------------------------------------------------------------------
		// --- update $U_{u}$
		for(int f=0; f<d; f++)
		{
			U[u][f] += gamma * ( lambda*(loss_uic * (V[i][f] - V[c][f]) + loss_uij * (V[i][f] - V[j][f])  + loss_upc * (V[p][f] - V[c][f]) + loss_upj * (V[p][f] - V[j][f])) + (1-lambda)*loss_iuv * V[i][f]- alpha_u * U[u][f] );
		}
		// --- update $U_{l}$
		for(int f=0; f<d; f++)
		{
			U[l][f] += gamma * ( (1-lambda)*loss_ilv * V[i][f] - alpha_u * U[l][f] );
		}
		// --- update $U_{v}$
		for(int f=0; f<d; f++)
		{
			U[v][f] += gamma * ( (1-lambda)*(-1) * (loss_iuv + loss_ilv) * V[i][f] - alpha_u * U[v][f] );
		}
		// --- update $V_{i}$
		for (int f=0; f<d; f++)
		{
			V[i][f] += gamma * ( lambda*(loss_uic + loss_uij) * U[u][f] + (1-lambda)*(loss_iuv * (U[u][f]-U[v][f]) + loss_ilv * (U[l][f]-U[v][f])) - alpha_v * V[i][f] );
		}
		// --- update $V_{p}$
		for (int f=0; f<d; f++)
		{
			V[p][f] += gamma * ( lambda*(loss_upc + loss_upj) * U[u][f] - alpha_v * V[p][f] );
		}
		// --- update $V_{c}$
		for (int f=0; f<d; f++)
		{
			V[c][f] += gamma * ( lambda*(loss_uic + loss_upc) * U[u][f] * (-1) - alpha_v * V[c][f] );
		}
		// --- update $V_{j}$
		for (int f=0; f<d; f++)
		{
			V[j][f] += gamma * ( lambda*(loss_uij + loss_upj) * U[u][f] * (-1) - alpha_v * V[j][f] );
		}
		// --- update $b_i$
		biasV[i] += gamma * ( lambda*(loss_uic + loss_uij) - beta_v * biasV[i] );
		// --- update $b_p$
		biasV[p] += gamma * ( lambda*(loss_upc + loss_upj) - beta_v * biasV[p] );
		// --- update $b_c$
		biasV[c] += gamma * ( lambda*(loss_uic + loss_upc) * (-1) - beta_v * biasV[c] );
		// --- update $b_j$
		biasV[j] += gamma * ( lambda*(loss_uij + loss_upj) * (-1) - beta_v * biasV[j] );

		// --- update $b_u$
		biasU[u] += gamma * ( (1-lambda)*loss_iuv - beta_v * biasU[u] );
		// --- update $b_l$
		biasU[l] += gamma * ( (1-lambda)*loss_ilv - beta_v * biasU[l] );
		// --- update $b_v$
		biasU[v] += gamma * ( (1-lambda)*loss_iuv * (-1) - beta_v * biasU[v] );
		
		// ---------------------------------------------------
		float bpr_loss = lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuic))) + lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xuij)))+ lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xupc)))+ lambda*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xupj))) +(1-lambda)*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xilv)))+(1-lambda)*(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -r_Xiuv)));
		float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(U[l])+norm(U[v])+norm(V[i])+norm(V[p])+norm(V[c])+norm(V[j])+biasU[u]*biasU[u]+biasU[l]*biasU[l]+biasU[v]*biasU[v]+biasV[i]*biasV[i]+biasV[p]*biasV[p]+biasV[c]*biasV[c]+biasV[j]*biasV[j]));
		loss_ipcj_ulv = bpr_loss - reg_loss;

		return loss_ipcj_ulv;

	}
	// =========================================================
	public static void loss_test() {
		System.out.println("loss_test");
		float total_loss = 0f;
		for (int iter_rand = 1; iter_rand <= n; iter_rand++){			
			// ------------------------------- u --------------------------------
			int u = iter_rand;
			if (!TrainData.containsKey(u))
				continue;
			// ==================================================================
			// ------------------------------ Iu_i -------------------------------
			List<Integer> Iu_i = new ArrayList<Integer>(TrainData.get(u).keySet());    // === Iu_i
			int Size_Iu_i = Iu_i.size();
			// --- randomly sample an item $i$, Math.random(): [0.0, 1.0)
			int t = (int) Math.floor(Math.random()*Size_Iu_i);
			int i = Iu_i.get(t);                                                       // === i
		//    	System.out.println("从Iu_i中随机选取交互物品i: " + i);
		
			// ----------------------------- Iu_j --------------------------------
			List<Integer> Iu_j = new ArrayList<Integer>(Iu_j_data.get(u));			   // === Iu_j
			int Size_Iu_j = Iu_j.size();
			int t1 = (int) Math.floor(Math.random()*Size_Iu_j);
			int j = Iu_j.get(t1);                                                      // === j
		
			// ----------------------------- Iu_p --------------------------------
			List<Integer> Iu_p = new ArrayList<Integer>(Iu_p_data.get(u));     		   // === Iu_p
			int Size_Iu_p = Iu_p.size();
		
			// ----------------------------- Iu_uk --------------------------------
			List<Integer> Iu_uk = new ArrayList<Integer>(Iu_uk_data.get(u));           // === Iu_uk
			int Size_Iu_uk = Iu_uk.size();
		
			// =========================================================
			// 如果Iu_*可分，则模型为四分模型： Iu_i>Iu_uk,Iu_i>Iu_j,Iu_p>Iu_uk,Iu_p>Iu_j
			// 如果Iu_*不可分，则模型为三分模型：Iu_i>Iu_uk,Iu_i>Iu_j
			// =========================================================
			if(Size_Iu_p == 0)  // 三分模型：Iu_i>Iu_uk,Iu_i>Iu_j
			{
				// --- randomly sample an item $uk$
				int t2 = (int) Math.floor(Math.random()*Size_Iu_uk);
				int uk = Iu_uk.get(t2);                                                // === uk
		
				// -------------------------------------------------------------------------------------
				// ---calculate loss_
				float r_ui = biasV[i];
				float r_uk = biasV[uk];
				float r_uj = biasV[j];
				for (int f=0; f<d; f++)
				{
					r_ui += U[u][f] * V[i][f];
					r_uk += U[u][f] * V[uk][f];
					r_uj += U[u][f] * V[j][f];
				}
				float r_Xuik = r_ui - r_uk;
				float r_Xuij = r_ui - r_uj;						
				// ---------------------------------------------------
				float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -1 * r_Xuik))) + (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -1*r_Xuij)));
				float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(V[i])+norm(V[uk])+norm(V[j])+biasV[i]*biasV[i]+biasV[uk]*biasV[uk]+biasV[j]*biasV[j]));
				total_loss += bpr_loss - reg_loss;
				// ===================================================
			}
			else  // 四分模型：Iu_i>Iu_uk,Iu_i>Iu_j,Iu_p>Iu_uk,Iu_p>Iu_j
			{
				// --- randomly sample an item $l$
				int t3 = (int) Math.floor(Math.random()*Size_Iu_p);
				int l = Iu_p.get(t3);                                                  // === l
		//        	System.out.println("从Iu_p中随机选取物品l_id: " + l_id + "\t" + Iu_p.contains(l_id));
		
				// --- randomly sample an item $uk$
				int t4 = (int) Math.floor(Math.random()*Size_Iu_uk);
				int uk = Iu_uk.get(t4);                                                // === uk
				// ------------------------------------------------------------------------------------------------
				// --- calculate loss_
				float r_ui = biasV[i];
				float r_ul = biasV[l];
				float r_uk = biasV[uk];
				float r_uj = biasV[j];
				for (int f=0; f<d; f++)
				{
					r_ui += U[u][f] * V[i][f];
					r_ul += U[u][f] * V[l][f];
					r_uk += U[u][f] * V[uk][f];
					r_uj += U[u][f] * V[j][f];
				}
				float r_Xuik = r_ui - r_uk;
				float r_Xuij = r_ui - r_uj;
				float r_Xulk = r_ul - r_uk;
				float r_Xulj = r_ul - r_uj;
				// ---------------------------------------------------
				float bpr_loss = (float) Math.log(1f / (1f + (float) Math.pow(Math.E, -1*r_Xuik)))+(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -1*r_Xuij)))+(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -1*r_Xulk)))+(float) Math.log(1f / (1f + (float) Math.pow(Math.E, -1*r_Xulj)));
				float reg_loss = (float) (0.5*alpha_u*(norm(U[u])+norm(V[i])+norm(V[l])+norm(V[uk])+norm(V[j])+biasV[i]*biasV[i]+biasV[l]*biasV[l]+biasV[uk]*biasV[uk]+biasV[j]*biasV[j]));
				total_loss += bpr_loss - reg_loss;
				// ===================================================
			}
		}
		total_loss = -total_loss / (float) n;
		System.out.println("total_loss: " + total_loss);
	}  
    // =========================================================
    public static double sum(Collection<Integer> list1) {
		
		double result = 0;
		int num;
		
		
		List<Integer> line1 = new ArrayList<Integer>(list1);
		num = list1.size();
		for(int i=0; i<num; i++)
		{
			result += line1.get(i)*line1.get(i);
		}

		return result;	
	}
	// =============================================
	public static float norm(float[] aa) {
    	float res = 0f;
    	for(float i :aa) {
    		res += i * i;
    	}
    	return (float) Math.sqrt(res);
    }
    
    // =============================================================
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void testRanking(HashMap<Integer, HashSet<Integer>> TestData)
    {
		// TestData: user->items 
		// ==========================================================
		float[] PrecisionSum = new float[topK+1];
		float[] RecallSum = new float[topK+1];	
		float[] F1Sum = new float[topK+1];
		float[] NDCGSum = new float[topK+1];
		float[] OneCallSum = new float[topK+1];
		float MRRSum = 0;
		float MAPSum = 0;
		float ARPSum = 0;
		float AUCSum = 0;
		float[] result_evaluation = new float[6];   // 记录6个指标结果
		// --- calculate the best DCG, which can be used later
		float[] DCGbest = new float[topK+1];
		for (int k=1; k<=topK; k++)
		{
			DCGbest[k] = DCGbest[k-1];
			DCGbest[k] += 1/Math.log(k+1); // 提前计算好IDCG（best DCG）
		}
		
		// --- number of test cases
    	int UserNum_TestData = TestData.keySet().size(); // test集用户个数
    	
    	for(int u=1; u<=n; u++)
    	{
    		// --- check whether the user $u$ is in the test set
    		if (!TestData.containsKey(u))
    			continue;
    		
    		// ---
    		Set<Integer> ItemSet_u_TrainData = new HashSet<Integer>(); // 训练集里面用户交互过的所有物品集
    		if (TrainData.containsKey(u))
    		{
    			ItemSet_u_TrainData = TrainData.get(u).keySet();
    		}
    		HashSet<Integer> ItemSet_u_TestData = TestData.get(u); // 测试集里面用户交互过的所有物品集

    		// --- the number of preferred items of user $u$ in the test data 
    		int ItemNum_u_TestData = ItemSet_u_TestData.size();   // 测试集用户交互过的物品个数
    		
    		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++    		
    		// --- prediction    		
    		HashMap<Integer, Float> item2Prediction = new HashMap<Integer, Float>();
    		item2Prediction.clear();
    		
    		for(int i=1; i<=m; i++)
    		{
    			// --- (1) check whether item $i$ is in the whole item set
    			// --- (2) check whether item $i$ appears in the training set of user $u$
    			// --- (3) check whether item $i$ is in the ignored set of items    			
    			if ( !ItemSetWhole.contains(i) || ItemSet_u_TrainData.contains(i) )  // 只找训练集里用户没有交互过的物品集来预测
    				continue;

    			// --- prediction via inner product
        		float pred = biasV[i];
        		for (int f=0; f<d; f++)
        		{
        			pred += U[u][f]*V[i][f];
        		}       			
        		item2Prediction.put(i, pred);
        	}
    		// --- sort
    		List<Map.Entry<Integer,Float>> listY = 
    				new ArrayList<Map.Entry<Integer,Float>>(item2Prediction.entrySet()); 		
    		Collections.sort(listY, new Comparator<Map.Entry<Integer,Float>>()
    		{
    			public int compare( Map.Entry<Integer, Float> o1, Map.Entry<Integer, Float> o2 )   
    			{
    				return o2.getValue().compareTo( o1.getValue() ); // 降序排序
    			}
    		});
    		
    		// ===========================================================
    		// === Evaluation: TopK Result 
    		// --- Extract the topK recommended items
    		int k=1;
    		int[] TopKResult = new int [topK+1];    		
    		Iterator<Entry<Integer, Float>> iter = listY.iterator();
    		while (iter.hasNext())
    		{
    			if(k>topK)
    				break;
    			
    			Map.Entry<Integer, Float> entry = (Map.Entry<Integer, Float>) iter.next(); 
    			int itemID = entry.getKey();
    			TopKResult[k] = itemID;
    			k++;
    		}
    		// --- TopK evaluation
    		int HitSum = 0;
    		float[] DCG = new float[topK+1];
    		float[] DCGbest2 = new float[topK+1];
    		for(k=1; k<=topK; k++)
    		{
    			// ---
    			DCG[k] = DCG[k-1];
    			int itemID = TopKResult[k];
    			if ( ItemSet_u_TestData.contains(itemID) )
    			{
        			HitSum += 1;
        			DCG[k] += 1 / Math.log(k+1);
    			}
    			// --- precision, recall, F1, 1-call
    			float prec = (float) HitSum / k;
    			float rec = (float) HitSum / ItemNum_u_TestData;    			
    			float F1 = 0;
    			if (prec+rec>0)
    				F1 = 2 * prec*rec / (prec+rec);
    			PrecisionSum[k] += prec;
    			RecallSum[k] += rec;
    			F1Sum[k] += F1;
    			// --- in case the the number relevant items is smaller than k 
    			if (ItemSet_u_TestData.size()>=k)
    				DCGbest2[k] = DCGbest[k];
    			else
    				DCGbest2[k] = DCGbest2[k-1];
    			NDCGSum[k] += DCG[k]/DCGbest2[k];
    			// ---
    			OneCallSum[k] += HitSum>0 ? 1:0; 
    		}
    		// ===========================================================
    		
    		// ===========================================================
    		// === Evaluation: Reciprocal Rank
    		if (flagMRR)
    		{
	    		int p = 1;
	    		iter = listY.iterator();    		
	    		while (iter.hasNext())
	    		{	
	    			Map.Entry<Integer, Float> entry = (Map.Entry<Integer, Float>) iter.next(); 
	    			int itemID = entry.getKey();
	    			
	    			// --- we only need the position of the first relevant item
	    			if(ItemSet_u_TestData.contains(itemID))    				
	    				break;
	
	    			p += 1;
	    		}
	    		MRRSum += 1 / (float) p;
    		}
    		// ===========================================================
    		
    		// ===========================================================
    		// === Evaluation: Average Precision
    		if (flagMAP)
    		{
	    		int p = 1; // the current position
	    		float AP = 0;
	    		int HitBefore = 0; // number of relevant items before the current item
	    		iter = listY.iterator();    		
	    		while (iter.hasNext())
	    		{	
	    			Map.Entry<Integer, Float> entry = (Map.Entry<Integer, Float>) iter.next(); 
	    			int itemID = entry.getKey();
	    			
	    			if(ItemSet_u_TestData.contains(itemID))
	    			{
	    				AP += 1 / (float) p * (HitBefore + 1);
	    				HitBefore += 1;
	    			}
	    			p += 1;
	    		}
	    		MAPSum += AP / ItemNum_u_TestData;
    		}
    		// ===========================================================
    		
    		// ===========================================================
    		// --- Evaluation: Relative Precision
    		if (flagARP)
    		{
	    		int p = 1; // the current position
	    		float RP = 0;    		
	    		iter = listY.iterator();    		
	    		while (iter.hasNext())
	    		{
	    			Map.Entry<Integer, Float> entry = (Map.Entry<Integer, Float>) iter.next(); 
	    			int itemID = entry.getKey();
	    			
	    			if(ItemSet_u_TestData.contains(itemID))
	    				RP += p;
	    			p += 1;
	    		}
	    		// ARPSum += RP / ItemSetWhole.size() / ItemNum_u_TestData;
	    		ARPSum += RP / item2Prediction.size() / ItemNum_u_TestData;
    		}
    		// ===========================================================
    		
    		// ===========================================================
    		// --- Evaluation: AUC
    		if (flagAUC)
    		{
	    		int AUC = 0; 
	    		for (int i: ItemSet_u_TestData)  // 遍历测试集用户U交互过的所有物品里
				{
					float r_ui = item2Prediction.get(i);  // 测试集里面用户u交互过的物品i的评分
					
					for( int j: item2Prediction.keySet() )  // item2Prediction 是训练集里用户u没有交互过的物品集，也就是整个测试集（包含里用户u交互过和没交互过的物品）
		    		{	
		    			if( !ItemSet_u_TestData.contains(j) )
		    			{
		    				float r_uj = item2Prediction.get(j);
		    				if ( r_ui > r_uj )
							{
								AUC += 1;
							}
		    			}
		    		}
				}
	    		   		
	    		AUCSum += (float) AUC / (item2Prediction.size() - ItemNum_u_TestData) / ItemNum_u_TestData;
    		}
    		// ===========================================================
    		
    	}
    	
    	// =========================================================
    	// --- the number of users in the test data
//    	System.out.println( "The number of users in the test data: " + Integer.toString(UserNum_TestData) );
    	
    	// --- precision@k
//    	System.out.println("--------pre--------");
    	for(int k=topK; k<=topK; k++)
    	{
    		float prec = PrecisionSum[k]/UserNum_TestData;
    		Pre_ave += prec;
//    		System.out.println("Prec@"+Integer.toString(k)+":"+Float.toString(prec));   
    		System.out.println(Float.toString(prec)); 
			result_evaluation[0] = prec;
    	}
    	// --- recall@k
//    	System.out.println("--------rec--------");
    	for(int k=topK; k<=topK; k++)
    	{
    		float rec = RecallSum[k]/UserNum_TestData;
    		Rec_ave += rec;
//    		System.out.println("Rec@"+Integer.toString(k)+":"+Float.toString(rec));  
    		System.out.println(Float.toString(rec));
			result_evaluation[1] = rec;
    	}
    	// --- F1@k
//    	System.out.println("--------F1--------");
    	for(int k=topK; k<=topK; k++)
    	{
    		float F1 = F1Sum[k]/UserNum_TestData;
    		F1_ave += F1;
//    		System.out.println("F1@"+Integer.toString(k)+":"+Float.toString(F1));   
    		System.out.println(Float.toString(F1));  
			result_evaluation[2] = F1;
    	}
    	// --- NDCG@k
//    	System.out.println("--------NDCG--------");
    	for(int k=topK; k<=topK; k++)
    	{
    		float NDCG = NDCGSum[k]/UserNum_TestData;
    		NDCG_ave += NDCG;
//    		System.out.println("NDCG@"+Integer.toString(k)+":"+Float.toString(NDCG));
    		System.out.println(Float.toString(NDCG));
			result_evaluation[3] = NDCG;
    	}
    	// --- 1-call@k
//    	for(int k=topK; k<=topK; k++)
//    	{
//    		float OneCall = OneCallSum[k]/UserNum_TestData;
//    		Onecall_ave += OneCall;
//    		System.out.println("1-call@"+Integer.toString(k)+":"+Float.toString(OneCall));    		
//    	}
    	// --- MRR
    	float MRR = MRRSum/UserNum_TestData;
    	MRR_ave += MRR;
//    	System.out.println("MRR:" + Float.toString(MRR));
    	System.out.println(Float.toString(MRR));
		result_evaluation[4] = MRR;
    	// --- MAP
    	float MAP = MAPSum/UserNum_TestData;
    	MAP_ave += MAP;
//    	System.out.println("MAP:" + Float.toString(MAP));
    	System.out.println(Float.toString(MAP));
		result_evaluation[5] = MAP;
    	// --- ARP
//    	float ARP = ARPSum/UserNum_TestData;
//    	ARP_ave += ARP;
//    	System.out.println("ARP:" + Float.toString(ARP));
    	// --- AUC
//    	float AUC = AUCSum/UserNum_TestData;
//    	AUC_ave += AUC;
//    	System.out.println("AUC:" + Float.toString(AUC));
    	// =========================================================
    	// ========================记录结果========================
		try {
    		
			System.out.println("正在写入文件(result_evaluation)..........");
			
			// 创建两个输出流控制方式
			PrintStream out = System.out;

			PrintStream ps = new PrintStream(fnresult_evaluation);
			System.setOut(ps);
			
			for(int i=0; i<result_evaluation.length; i++) 
			{							
				System.out.println(result_evaluation[i]);					
			}
			System.setOut(out);
			System.out.println("文件(topK_result)写入完毕，请查看相应的文件。");
    		
    	}catch(FileNotFoundException e) {
    		e.printStackTrace();
    	}
	  // ==================================================
    }
    
}
	
