import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Objects; 
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Accident {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, IntWritable>{

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      			


		String line = value.toString();
            	// split line and store here
            	// change , to tab
            	String[] mdata = line.split(",");
            		
            	// String mdata[0] is center id
		// String mdata[1] is date with YYYYMMDD format
		// String mdata[2] is parameter eg. SNWD= Snow Depth
		// String mdata[3] is Snow Depth value
		

    			

		//check	
		if(line !=null && !line.isEmpty() )//&& ( Objects.equals(mdata[13], "HUMAN") || Objects.equals(mdata[15], "ENVMT")))
		// pass center-id and first 6 characters of date in Text key
		// pass SNWD value in IntWritable value after converting from string to int
		// check for "-"
		{
		int i=0;
		String O0="", O4="", O14="";
		try{
       				i = Integer.parseInt(mdata[10]);
    		
       				O0 = mdata[0].toString();
       				O4 = mdata[4].toString();
       				O14 =  mdata[14].toString();

				context.write(new Text(O0 + "\t" + O4 +"\t"+ O14 ), new IntWritable(i));

    		}catch(Exception ex){ // handle your exception
    		
    		
    		}
		
		           	
 		}
      }
    }
  
  public static class IntSumReducer
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
      //context.write(new Text("city"), new IntWritable(1));
      // sum stores sum of Snow Depth
      int sum = 0;
      
      for (IntWritable val : values) {
        
        sum += val.get();
       
      }
      /*if(count == 0)
      	count++;*/
      
      
      result.set(sum);
      context.write(key, result);
    }
  }


public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(Accident.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }

}  


