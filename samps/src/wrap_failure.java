import java.util.*;
public class wrap_failure {
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner k=new Scanner(System.in);
		int num=k.nextInt();
		int cha=k.nextInt();
		String word;
		boolean first=true;
		String ender="";
		int count=0;
		for(int i=0; i<=num; i++)
		{
			String line=k.nextLine();
			Scanner reader=new Scanner(line);
			while(reader.hasNext())
			{
				word=reader.next();
				if (word.length()>cha)
				{
					ender="error";
					break;
				}
				else if(count+word.length()<=cha)
				{
					if (first)
					{
						ender+=word;
						count=word.length();
						first=false;
					}
					else
					{
						ender+=" "+word;
						count+=word.length()+1;
					}
				}
				else
				{
					ender+="\n"+word+" ";
					count=word.length()+1;
				}
			}
		}
		System.out.println(ender);
	}
}
