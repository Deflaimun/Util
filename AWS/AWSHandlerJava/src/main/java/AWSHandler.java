import java.io.*;

import java.net.InetAddress;
import java.net.SocketException;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.net.ftp.FTPClient;



public class AWSHandler {

    public static void main (String[] args) throws SocketException, IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        System.out.println("Digite o sourcebucket da amazon");
        String sourcebucket = br.readLine();
        System.out.println("Digite o sourcekey da amazon");
        String sourcekey = br.readLine();

        System.out.println("Realizando teste de upload");

        amazonUpload(sourcebucket,sourcekey,createSampleFile());


    }
    public void  FTPTransfer() throws IOException {
        //exemplo em https://commons.apache.org/proper/commons-net/examples/ftp/FTPClientExample.java
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        FTPClient ftp = new FTPClient();
        System.out.println("Digite o IP");
        InetAddress ip = InetAddress.getByName(br.readLine());
        System.out.println("Digite usuário");
        String usuario = br.readLine();
        System.out.println("Digite a senha");
        String senha = br.readLine();
        String caminhoArq = System.getProperty("user.dir");

        System.out.println("Digite a porta");
        int porta = br.read();

        ftp.connect(ip,porta);
        //por default a conexão é feita de modo passivo
        ftp.login( usuario, senha);

        System.out.println("Qual é o nome do arquivo?");
        caminhoArq += br.readLine();

        FileInputStream arqEnviar =

                new FileInputStream(caminhoArq);

        if (ftp.storeFile (caminhoArq, arqEnviar))

            System.out.println("Arquivo armazenado com sucesso!");

        else

            System.out.println ("Erro ao armazenar o arquivo.");



    }



    public static S3Object amazonDownload(String sourcebucket, String sourcekey){
        AmazonS3 s3 = new AmazonS3Client();
        S3Object object = null;

        try {

            System.out.println("Downloading an object");
            object = s3.getObject(new GetObjectRequest(sourcebucket, sourcekey));
            System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
            displayTextInputStream(object.getObjectContent());


        }
        catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        catch (IOException io){
            System.out.println("Problema no arquivo de download "+io.getMessage());

        }

        return object;

    }

    public static void amazonUpload(String sourcebucket, String sourcekey, File file){

        AmazonS3 s3 = new AmazonS3Client();

        try {

            System.out.println("Uploading a new object to S3 from a file\n");
            s3.putObject(new PutObjectRequest(sourcebucket, sourcekey, file));


        }
        catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }




    }

    private static File createSampleFile() throws IOException {
        File file = File.createTempFile("aws-java-sdk-", ".txt");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.write("01234567890112345678901234\n");
        writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
        writer.write("01234567890112345678901234\n");
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.close();

        return file;
    }

    private static void displayTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            System.out.println("    " + line);
        }
        System.out.println();
    }

}