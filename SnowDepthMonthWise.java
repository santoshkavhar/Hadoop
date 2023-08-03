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

public class SnowDepthMonthWise {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, IntWritable>{

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      			

		String line = value.toString();
            	// split line and store here
            	String[] mdata = line.split(",");
            		
            	// String mdata[0] is center id
		// String mdata[1] is date with YYYYMMDD format
		// String mdata[2] is parameter eg. SNWD= Snow Depth
		// String mdata[3] is Snow Depth value
					
		if(line !=null && !line.isEmpty() && Objects.equals(mdata[2], "SNWD"))
		// pass center-id and first 6 characters of date in Text key
		// pass SNWD value in IntWritable value after converting from string to int
		// check for "-"
		context.write(new Text(mdata[0].substring(0,2) +"\t"+ mdata[1].substring(4,6) + "\t"+ mdata[1].substring(0,4)), new IntWritable(Integer.parseInt(mdata[3])));
		            	
 
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
      // to count no. of instances of Snow Depth values
      int count = 0;
      // To calculate the average Snow Depth Calculated
      int avg = 0;
      
      for (IntWritable val : values) {
        
        sum += val.get();
        count++;
      }
      /*if(count == 0)
      	count++;*/
      avg = sum/count;
      
      result.set(avg);
      context.write(key, result);
    }
  }


public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(SnowDepthMonthWise.class);
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



/*


map(Obj key, Text value, Con con)
{
	String line= value.toString();
//split line and store here	
line1 split
	if(line !=null && !line.isEmpty() && Objects.equal(mdata[], "SNWD")
		//one extra
		context.write(new Text(mdata[1] + mdata[3].substring(0,6), new IntWritable(Integer.parseInt(mdata[])) ))

}


reduce(Text key, Itearble<IntWritable> values, Context context)
{
int sum=0;
for(IntWritable val : values){
	sum += val.get(); 
}
result.set(sum);
contect.write(key, result);


}



String s = "12,23";
String[] array = s.split(",");
Text t1 = new Text(array[0]);
Text t2 = new Text(array[1]);

*/
