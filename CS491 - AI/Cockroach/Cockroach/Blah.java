package cockroach;

public class Blah {
	public static void main(String[] args) {
		double temp = 2;
		int i = 0;
		while(temp > 0) {
			
		System.out.println("temp: " + temp);
		System.out.println(Math.exp((9 - 80)/temp));
		temp = temp - .1;
		i++;
		
		}
		System.out.println(i);
	}
}
