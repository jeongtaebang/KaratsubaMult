import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Karatsuba Multiplication with two input strings,
 * allow very long input integers beyond typical machine word size for integers
 * Outputs the product of the two inputs in String
 * @author Jeong Tae Bang
 * Nov 26, 2017
 */
public class KaratsubaMult {
	
	/**
	 * Karatsuba Multiplication implementation
	 * @param a, b : input strings of digit-length >= 1
	 * @return     : a * b in String
	 */
	private static String ktmult(String a, String b) {
		int size_a = a.length();
		int size_b = b.length();
		
		if ((size_a + size_b <= 3)) { // 1and1 or 2and1 digits
			return digit_mult(a,b);
		} 
		else {      
			if (size_a > size_b)
				b = digit_padder(a, b);
			else if (size_a < size_b)
				a = digit_padder(b, a);
			int n = Math.max(a.length(), b.length());
			
			// split rule : floor(n / 2) | ceiling(n / 2)
			String a_1 = a.substring(0, n / 2); // a
			String a_2 = a.substring(n / 2, n); // b
			
			String b_1 = b.substring(0, n / 2); // c
			String b_2 = b.substring(n / 2, n); // d
	
			String p = digit_adder(a_1, a_2);   // p = a + b
			String q = digit_adder(b_1, b_2);   // q = c + d
			
			String k_1 = ktmult(a_1, b_1);      // ac
			String k_2 = ktmult(a_2, b_2);      // bd
			String k_3 = ktmult(p, q);          // pq
			
			String temp = digit_subtracter(k_3, k_1);  
			String k_4  = digit_subtracter(temp, k_2); // (pq - ac) - bd
			
			/* Pad "0"s to k_1 and k_4 following the rule : 
			 * 10^(n or n+1)* k_1 + 10^(ceil(n/2))* k_4 + k_2 
			 */
			double m = (double) n;
			for (int i = 0; i < 2 * Math.ceil(m / 2); i++) {
				if (i < Math.ceil(m / 2))
					k_4 += "0";
				k_1 += "0";
			}
			String temp2 = digit_adder(k_1, k_4);
			
			return digit_adder(temp2, k_2);
		}
	}
	
	/**
	 * Multiply small a and b, converting between integer and string
	 * @param a, b : 1 or 2 digit integers in String
	 * @return     : a * b in String
	 */
	private static String digit_mult(String a, String b) {
		return Integer.toString( Integer.parseInt(a) * Integer.parseInt(b) );
	}
	
	/**
	 * Pad b with "0"s in front to match the length of a
	 * @param a, b : two integers in String, a is longer than b
	 * @return     : b with "0" padded in front 
	 */
	private static String digit_padder(String a, String b) {
		int size = Math.abs(a.length() - b.length());
		for (int i = 1; i <= size; i++)
			b = "0" + b;
		return b;
	}
	
	/**
	 * Subtract b from a and return the result
	 * @param a, b : two integers, a is greater than b
	 * @return     : a - b in String
	 */
	private static String digit_subtracter(String a, String b) {
		int size_a = a.length();  
		int size_b = b.length();
		int size = Math.max(size_a, size_b);
		// match the digit-lengths of a_1 and a_2
		if (size_a > size_b)
			b = digit_padder(a, b);
		else if (size_a < size_b) 
			a = digit_padder(b, a);
		
		// subtract b from a digit-by-digit from the back
		String result = "";
		String[] a_carry_board = a.split(""); // look here for carry managing

		for (int i = size - 1; i >= 0; i--) {
			int a_digit = Integer.parseInt(a_carry_board[i]);
			int b_digit = Integer.parseInt( Character.toString( b.charAt(i) ) );
			int temp_sub;
			
			if (b_digit > a_digit) {
				// borrow carry from a[i-1]
				a_digit += 10;
				int j = i - 1;
				// keep borrowing till non-"0"
				while (a_carry_board[j].equals("0")) {
					a_carry_board[j] = "9";
					j--;
				}
				a_carry_board[j] = Integer.toString(
						              Integer.parseInt(a_carry_board[j]) - 1
						                  ); // never reaches a[-1]
			}
			temp_sub = a_digit - b_digit;
			result = Integer.toString(temp_sub) + result;
		}
		return result;
	}
	
	/**
	 * Add a_1 and a_2 and return the sum
	 * @param a_1, a_2 : two integers in String, digit-length >= 1
	 * @return         : a_1 + a_2 in String
	 */
	private static String digit_adder(String a_1, String a_2) {
		int size_a = a_1.length(); 
		int size_a_2 = a_2.length();
		int size = Math.max(size_a, size_a_2);
		
		// match the digit-lengths of a_1 and a_2 
		if (size_a > size_a_2) 
			a_2 = digit_padder(a_1, a_2);
		else if (size_a < size_a_2)
			a_1 = digit_padder(a_2, a_1);
		
		// add string digit by digit from the back by
		int carry = 0;
		String result = ""; // prepend to result
		int last_idx = 0;   // temp_sum is 1 or 2 digits
		for (int i = size - 1; i >= 0 ; i--) {
			// add the integer-fied digits + carry
			int temp_sum = Integer.parseInt( Character.toString( a_1.charAt(i) ) ) +
			           Integer.parseInt( Character.toString( a_2.charAt(i) ) ) + 
			           carry;
			if (temp_sum >= 10) {
				carry = 1;
				last_idx = 1; // temp_sum is 2 digits
			} else {
				carry = 0;
				last_idx = 0;
			}
			result = Integer.toString(temp_sum).substring(last_idx) + result;
		}
		if (carry != 0) result = "1" + result; // last carry if necessary
		return result;
	}
	
	public static void main(String[] args) throws IOException {
		// register and read input values 
		BufferedReader input = new BufferedReader(new InputStreamReader (System.in));
		StringTokenizer tk = new StringTokenizer(input.readLine());
		String a = tk.nextToken();
		String b = tk.nextToken();
		/* test
		 * String a = "3141592653589793238462643383279502884197169399375105820974944592";
		 * String b = "2718281828459045235360287471352662497757247093699959574966967627";
		 */

		System.out.println(ktmult(a, b));
	}

}

