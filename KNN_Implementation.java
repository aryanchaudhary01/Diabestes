package knn;
import java.util.*;
import java.io.*;

public class KNN_Implementation {
	
	// created lists for storing training and testing datasets label and features.

		private List<double[]> trainfeatures = new ArrayList<>();
		private List<String> trainlabel = new ArrayList<>();

		private List<double[]> testfeatures = new ArrayList<>();
		private List<String> testlabel = new ArrayList<>();
		/*
		 * sc object for getting user input
		 */

		Scanner sc = new Scanner(System.in);
		int knn_value = 1;
		int DistanceMetricsSelection = 0;
		int totalNumberOfLabel = 0;
		/*
		 * method to get K value and Distance metrics.
		 */
		void getKValueandDistMetrics()                       
		{
			System.out.println("Enter the K value of KNN ");
			knn_value = sc.nextInt();
			// Restricted k value less 30.
			if (knn_value > 30)
			 {
				System.out.println("K Value is out of range.");
				getKValueandDistMetrics();
			}
			 else 
			{
				System.out.println("Select below distance metric(1 or 2)\n1 Euclidean Distance Metrics\n2 Manhattan Distance Metrics");
				DistanceMetricsSelection = sc.nextInt();

			}
		}
		/*
		 * loading testing data and extracting features and label for training dataset
		 * 
		 */
		void loadtestData(String testfilename) throws NumberFormatException, IOException {
			File testfile = new File(testfilename);      //to store test datset
			try {
				BufferedReader testreadFile = new BufferedReader(new FileReader(testfile));             //get the input from dataset into testreadfile
				PrintWriter pw = new PrintWriter("RealTestLabel.txt");                              // to make a new realtestlabel.txt file to store the diabestes present or not from the dataset
				String testline;
				while ((testline = testreadFile.readLine()) != null)                           // testline stores each line of csv and also checks the if the testreadline empty or not
				 {
					String[] split = testline.split(",");                                     //to seperatly store the value of each line in dataset in an array
					double[] feature = new double[split.length - 1];						// new array to string value into double value
					for (int i = 0; i < split.length - 1; i++)
					{ if (split[i]==""|| split[i]==null)
					split[i]=Integer.toString(cleanData(testfilename,i));
						feature[i] = Double.parseDouble(split[i]);}
					testfeatures.add(feature);
					testlabel.add(split[feature.length]);
					// writing original label for test data to file and counting number of label.
					pw.println(split[feature.length]);
					totalNumberOfLabel++;
				}
				pw.close();
				testreadFile.close();
			}
			catch (FileNotFoundException e) {
				// TODO Auto catch block
				e.printStackTrace();
			}
		}
		void loadtrainLabelData(String trainfilename) throws NumberFormatException, IOException {
			File trainfile = new File(trainfilename);
			try {
				BufferedReader trainreadFile = new BufferedReader(new FileReader(trainfile));
				PrintWriter pw = new PrintWriter("RealTrainLabel.txt");                          //this file is to store diabetes present or not in the person from the dataset
				String trainline;
				while ((trainline = trainreadFile.readLine()) != null) {
					String[] split = trainline.split(",");
					double[] feature = new double[split.length - 1];
					for (int i = 0; i < split.length - 1; i++)
					{ if (split[i]==""|| split[i]==null)
					split[i]=Integer.toString(cleanData(trainfilename,i));
						feature[i] = Double.parseDouble(split[i]);}
					trainfeatures.add(feature);
					trainlabel.add(split[feature.length]);										// adding the last colum i.e diabetes section from dataset 
					// writing original label for test data to file and counting number of label.
					pw.println(split[feature.length]);
					totalNumberOfLabel++;
				}
				pw.close();
				trainreadFile.close();
			}
			catch (FileNotFoundException e) {
				// TODO Auto catch block
				e.printStackTrace();
			}
		}
		/*
		 * Based on user input, calling algorithm to calculate distance
		 */
		void distanceCalculate() throws IOException {

			if (DistanceMetricsSelection == 1) {
				euclideanTrainDistance();
				euclideanTestDistance();
				// calling accuracy method to show accuracy of model.
				accuracy();
			}

			else if (DistanceMetricsSelection == 2) {
				manhattanTrainDistance();
				manhattanTestDistance();
				accuracy();
			}

			else {
				// if user selecting invalid options then they must select correct option.
				System.out.println("Invalid Selection");
				getKValueandDistMetrics();
				distanceCalculate();
			}
		}
		
		@SuppressWarnings("unchecked")
		void euclideanTrainDistance() throws FileNotFoundException {							
			KNN_Distance euclidean = new KNN_Distance();				//making an instance of KNN_Distance


			Iterator<double[]> trainOverfitITR = trainfeatures.iterator();				

			PrintWriter pw = new PrintWriter("EuclideanTrainResult.txt");		
			while (trainOverfitITR.hasNext()) {
				double trainOverfitF[] = trainOverfitITR.next();			// double array to store from list 
				Iterator<double[]> trainITR = trainfeatures.iterator();
				int noOfobject = 0;
				ArrayList<DistanceAndFeatures> ts = new ArrayList<>();
				while (trainITR.hasNext()) {
					double trainF[] = trainITR.next();
					double dist = 0;
					dist = euclidean.getEuclideanDistance(trainF, trainOverfitF);
					
					String trainFeat = trainlabel.get(noOfobject);			//to get a specific element from index of trainlabel
					DistanceAndFeatures DfObject = new DistanceAndFeatures(dist, trainFeat);
					ts.add(DfObject);
					Collections.sort(ts);
					noOfobject++;
					

				}

				/*
				 * counting top predicted label based on k value
				 */
				int flag = 0, positive = 0, negative = 0;

				while (flag < knn_value) {
					DistanceAndFeatures s = ts.get(flag);
					String s1 = s.getLabel();
					if (s1.equals("1"))
						positive++;
					else 
						negative++;
					flag++;

				}

				/*
				 * counting label and selecting highest label count as prediction label and
				 * writing to output file.
				 */
				if (positive > negative) {
					pw.println("1");

				} else 
					pw.println("0");
			}
			pw.close();
		}

		@SuppressWarnings("unchecked")
		void euclideanTestDistance() throws FileNotFoundException {
			KNN_Distance euclidean = new KNN_Distance();

			Iterator<double[]> testITR = testfeatures.iterator();

			PrintWriter pw = new PrintWriter("EuclideanTestResult.txt");

			while (testITR.hasNext()) {
				double testF[] = testITR.next();
				Iterator<double[]> trainITR = trainfeatures.iterator();
				int noOfobject = 0;
				ArrayList<DistanceAndFeatures> ts = new ArrayList<>();
				while (trainITR.hasNext()) {
					double trainF[] = trainITR.next();
					double dist = 0;
					dist = euclidean.getEuclideanDistance(trainF, testF);

					String trainFeat = trainlabel.get(noOfobject);
					DistanceAndFeatures DfObject = new DistanceAndFeatures(dist, trainFeat);
					ts.add(DfObject);
					Collections.sort(ts);
					noOfobject++;

				}

				/*
				 * counting top predicted label based on k value
				 */
				int flag = 0, positive = 0, negative = 0;

				while (flag < knn_value) {
					DistanceAndFeatures s = ts.get(flag);
					String s1 = s.getLabel();
					if (s1.equals("1"))
						positive++;
					else 
						negative++;
					flag++;

				}

				/*
				 * counting label and selecting highest label count as prediction label and
				 * writing to output file.
				 */
				if (positive > negative) {
					pw.println("1");

				} else 
					pw.println("0");
			}
			pw.close();
		}

		
		
		/*
		 * Manhattan Distance
		 * 
		 * Calling Manhattan method to calculate distance and writing output to file.
		 * 
		 */

		@SuppressWarnings("unchecked")
		void manhattanTrainDistance() throws FileNotFoundException {
			KNN_Distance euclidean = new KNN_Distance();

			Iterator<double[]> trainOverfitITR = trainfeatures.iterator();

			PrintWriter pw = new PrintWriter("ManhattanTrainResult.txt");
			while (trainOverfitITR.hasNext()) {
				double trainOverfitF[] = trainOverfitITR.next();
				Iterator<double[]> trainITR = trainfeatures.iterator();
				int noOfobject = 0;
				ArrayList<DistanceAndFeatures> ts = new ArrayList<>();
				while (trainITR.hasNext()) {
					double trainF[] = trainITR.next();
					double dist = 0;
					dist = euclidean.getManhattanDistance(trainF, trainOverfitF);

					String trainFeat = trainlabel.get(noOfobject);
					DistanceAndFeatures DfObject = new DistanceAndFeatures(dist, trainFeat);
					ts.add(DfObject);
					Collections.sort(ts);
					noOfobject++;

				}

				/*
				 * counting top predicted label based on k value
				 */

				int flag = 0, positive = 0, negative = 0;

				while (flag < knn_value) {
					DistanceAndFeatures s = ts.get(flag);
					String s1 = s.getLabel();
					if (s1.equals("1"))
						positive++;
					else 
						negative++;
					flag++;

				}

				/*
				 * counting label and selecting highest label count as prediction label and
				 * writing to output file.
				 */
				if (positive > negative) {
					pw.println("1");

				} else 
					pw.println("0");
			}
			pw.close();
		}

		
		@SuppressWarnings("unchecked")
		void manhattanTestDistance() throws FileNotFoundException {
			KNN_Distance euclidean = new KNN_Distance();

			Iterator<double[]> testITR = testfeatures.iterator();

			PrintWriter pw = new PrintWriter("ManhattanTestResult.txt");

			while (testITR.hasNext()) {
				double testF[] = testITR.next();
				Iterator<double[]> trainITR = trainfeatures.iterator();
				int noOfobject = 0;
				ArrayList<DistanceAndFeatures> ts = new ArrayList<>();
				while (trainITR.hasNext()) {
					double trainF[] = trainITR.next();
					double dist = 0;
					dist = euclidean.getManhattanDistance(trainF, testF);

					String trainFeat = trainlabel.get(noOfobject);
					DistanceAndFeatures DfObject = new DistanceAndFeatures(dist, trainFeat);
					ts.add(DfObject);
					Collections.sort(ts);
					noOfobject++;

				}

				/*
				 * counting top predicted label based on k value
				 */

				int flag = 0, positive = 0, negative = 0;

				while (flag < knn_value) {
					DistanceAndFeatures s = ts.get(flag);
					String s1 = s.getLabel();
					if (s1.equals("1"))
						positive++;
					else 
						negative++;
					flag++;

				}

				/*
				 * counting label and selecting highest label count as prediction label and
				 * writing to output file.
				 */
				if (positive > negative) {
					pw.println("1");

				} else 
					pw.println("0");
			}
			pw.close();
		}

		
		
		/*
		 * Calculating accuracy for model based Euclidean and Manhattan distance.
		 */
		void accuracy() throws IOException {
			int count = 0;
			int count1 = 0;
			int tp_test=0,fp_test=0,fn_test=0;
			int tp_train=0,fp_train=0,fn_train=0;;
			File fileTest = null;
			File fileTrain = null;
			if (DistanceMetricsSelection == 1) {
				fileTest = new File("EuclideanTestResult.txt");
				fileTrain = new File("EuclideanTrainResult.txt");
			}

			else if (DistanceMetricsSelection == 2) {
				fileTest = new File("ManhattanTestResult.txt");
				fileTrain = new File("ManhattanTrainResult.txt");

			}

			BufferedReader rf = new BufferedReader(new FileReader(fileTest));
			BufferedReader label = new BufferedReader(new FileReader(new File("RealTestLabel.txt")));
			String s = rf.readLine();
			while (s != null) {
				String lab = label.readLine();
				if (s.equals(lab)) {
					count1++;
					if(s.equals("1"))
					tp_test++;
				} else {
					if(s.equals("1"))
					fp_test++;
					else 
					fn_test++;
				}

				s = rf.readLine();
			}
			float precision_test=(((float) tp_test /(tp_test+fp_test)) * 100);
			float senstivity_test=(((float) tp_test/(tp_test+fn_test)) * 100);
			float fmeasure_test=((float)(2*(precision_test*senstivity_test))/(precision_test+senstivity_test));
			System.out.println("Test error is: " + (100-(((float) count1 /276) * 100)) + "%");
			System.out.println("Test Accuracy is: " + ((((float) count1 /276) * 100)) + "%");
			System.out.println("Test precision	 is: " +precision_test + "%");
			System.out.println("Test f measure	 is: " +(fmeasure_test) + "%");
			rf.close();
			label.close();
//			
			BufferedReader rf2 = new BufferedReader(new FileReader(fileTrain));
			BufferedReader label2 = new BufferedReader(new FileReader(new File("RealTrainLabel.txt")));
			String s2 = rf2.readLine();
			while (s2 != null) {
				String lab = label2.readLine();
				if (s2.equals(lab)) {
					count++;
					if(s2.equals("1"))
					tp_train++;

				} else {
					if(s2.equals("1"))
					fp_train++;
					else 
					fn_train++;
					
				}

				s2 = rf2.readLine();
			}
			float precision_train=(((float) tp_train /(tp_train+fp_train)) * 100);
			float senstivity_train=(((float) tp_train/(tp_train+fn_train)) * 100);
			float fmeasure_train=((float)(2*(precision_train*senstivity_train))/(precision_train+senstivity_train));
			System.out.println();
			System.out.println("Train error is: " + (100-(((float) count/676) * 100)) + "%");
			System.out.println("Train Accuracy is: " + ((((float) count/676) * 100)) + "%");
			System.out.println("Train precision	 is: " +precision_train + "%");
			System.out.println("Train f measure	 is: " +(fmeasure_train) + "%");
			rf2.close();
			label2.close();
		}
		int  cleanData(String trainfilename,int j) throws NumberFormatException, IOException {
			File trainfile = new File(trainfilename);
			String trainline;
			int f=j%15;
			Double sum=0.0;
			int d=0;
			
				BufferedReader trainreadFile = new BufferedReader(new FileReader(trainfile));
				while ((trainline = trainreadFile.readLine()) != null) {
					String[] split = trainline.split(",");
					for (int i = 0; i < split.length - 1; i++)
					{ if (i%15==f)
						{d++;
						if (split[i]==""|| split[i]==null)
						sum=sum+0;
				   else
						sum =sum+ Double.parseDouble(split[i]);}
											  
			   
				}}
				trainreadFile.close();
			return ((int)Math.round(sum/d));
				
			
			

		}
	}

