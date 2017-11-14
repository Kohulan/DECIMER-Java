
class Example {
	/** Let's assume that we have e.g. 8 core computer and need to run parallel section only twice */
	public static void main(String[] args) {

		// omp parallel threadNum(2)
		{
			System.out.println("This is printed only twice.");
		}
	}
}
