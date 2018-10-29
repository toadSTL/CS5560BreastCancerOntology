import java.io.*;


//Simple program to get all the data onto one line

public class PreProcData {
    public static void main(String [] args) {
            for(int i = 1; i < 31; i++ ){

                String inName = "data/abstracts/" + i + ".txt";
                String outName = "temp/" + i + ".txt";
                // This will reference one line at a time
                String line = null;
                try {
                    // FileReader reads text files in the default encoding.
                    FileReader fileReader =
                            new FileReader(inName);

                    // Always wrap FileReader in BufferedReader.
                    BufferedReader bufferedReader =
                            new BufferedReader(fileReader);

                    // Assume default encoding.
                    FileWriter fileWriter =
                            new FileWriter(outName);

                    // Always wrap FileWriter in BufferedWriter.
                    BufferedWriter bufferedWriter =
                            new BufferedWriter(fileWriter);


                    while((line = bufferedReader.readLine()) != null) {
                        bufferedWriter.write(line + " ");
                        System.out.println(line);
                    }

                    // Always close files.
                    bufferedWriter.close();
                    bufferedReader.close();
                }
                catch(FileNotFoundException ex) {
                    System.out.println(
                            "Unable to open file '" +
                                    "'");
                }
                catch(IOException ex) {
                    System.out.println(
                            "Error reading file '"
                                    +"'");
                    // Or we could just do this:
                    // ex.printStackTrace();
                }
            }
            // The name of the file to open.
        for(int i = 1; i < 11; i++ ){

            String inName = "data/abstracts/abs_" + i + ".txt";
            String outName = "temp/abs_" + i + ".txt";
            // This will reference one line at a time
            String line = null;
            try {
                // FileReader reads text files in the default encoding.
                FileReader fileReader =
                        new FileReader(inName);

                // Always wrap FileReader in BufferedReader.
                BufferedReader bufferedReader =
                        new BufferedReader(fileReader);

                // Assume default encoding.
                FileWriter fileWriter =
                        new FileWriter(outName);

                // Always wrap FileWriter in BufferedWriter.
                BufferedWriter bufferedWriter =
                        new BufferedWriter(fileWriter);


                while((line = bufferedReader.readLine()) != null) {
                    bufferedWriter.write(line + " ");
                    System.out.println(line);
                }

                // Always close files.
                bufferedWriter.close();
                bufferedReader.close();
            }
            catch(FileNotFoundException ex) {
                System.out.println(
                        "Unable to open file '" +
                                "'");
            }
            catch(IOException ex) {
                System.out.println(
                        "Error reading file '"
                                +"'");
                // Or we could just do this:
                // ex.printStackTrace();
            }
        }

        }

}
