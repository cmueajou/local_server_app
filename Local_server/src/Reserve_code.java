import java.util.Random;
import java.security.SecureRandom;
import java.math.BigInteger;


public class Reserve_code {
	 private SecureRandom random = new SecureRandom();
	 
	 public String nextSessionId() {
		    return new BigInteger(130, random).toString(32);
		  }
	 public String generate_Code(String nextSessionId){
		 int sub_index=0;
		 Random random=new Random();
		 sub_index=random.nextInt(24);
		 if(sub_index<12)
			 return nextSessionId.substring(sub_index,sub_index+8);
		 else
			 return nextSessionId.substring(sub_index-8,sub_index);
		 
	 }


}
