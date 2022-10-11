import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_core.Rect;


import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

import org.bytedeco.javacv.Frame;

public class App {
    public static void main(String args[]) throws FrameGrabber.Exception, InterruptedException {
        // Verificação das credenciais do usuário
        int numeroAmostras = 25;
        int amostra = 1;

        // Recolhendo Credenciais
        System.out.println("Digite seu Nome: ");
        Scanner cadastro = new Scanner(System.in);
        String nomePessoa = cadastro.nextLine();

        System.out.println("Digite seu Nível de Acesso: ");
        Scanner acessoNivel = new Scanner(System.in);
        int idAcesso = acessoNivel.nextInt();

        String fileName = "pessoa." + nomePessoa + " - " + idAcesso + ".jpg";

        String[] columnNames1 = {
            "First Name",
            "Last Name",
            "Sport",
            "# of Years",
            "Vegetarian"
        };

        Object[][] dataTable1 = {
            {"Nivel", "1",
             "Nivel", 1, false},
            {"Nivel", "1",
             "Nivel", 1, true},
            {"Nivel", "1",
             "Nivel", 1, false},
            {"Nivel", "1",
             "Nivel", 1, true},
            {"Nivel", "1",
             "Nivel", 1, false}
        };

        String[] columnNames2 = {
            "First Name",
            "Last Name",
            "Sport",
            "# of Years",
            "Vegetarian"
        };

        Object[][] dataTable2 = {
            {"Nivel", "2",
             "Nivel", 2, false},
            {"Nivel", "2",
             "Nivel", 2, true},
            {"Nivel", "2",
             "Nivel", 2, false},
            {"Nivel", "2",
             "Nivel", 2, true},
            {"Nivel", "2",
             "Nivel", 2, false}
        };

        String[] columnNames3 = {
            "First Name",
            "Last Name",
            "Sport",
            "# of Years",
            "Vegetarian"
        };

        Object[][] dataTable3 = {
            {"Nivel", "3",
             "Nivel", 3, false},
            {"Nivel", "3",
             "Nivel", 3, true},
            {"Nivel", "3",
             "Nivel", 3, false},
            {"Nivel", "3",
             "Nivel", 3, true},
            {"Nivel", "3",
             "Nivel", 3, false}
        };

        File folder = new File("src\\fotos\\");
        File[] listOfFiles = folder.listFiles();
        JFrame frame = new JFrame("TableDemo");

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].getName().equals(fileName)) {
                System.out.println("Usuário já cadastrado!");

                // Verificação do Nível de Acesso
                if (listOfFiles[i].getName().contains("1")) {
                    System.out.println("Usuário Nível 1");
                    JTable table = new JTable(dataTable1, columnNames1);
                    JScrollPane scrollPane = new JScrollPane(table);
                    frame.add(scrollPane);

                } else if (listOfFiles[i].getName().contains("2")) {
                    System.out.println("Usuário Nível 2");
                    JTable table = new JTable(dataTable2, columnNames2);
                    JScrollPane scrollPane = new JScrollPane(table);
                    frame.add(scrollPane);

                } else if (listOfFiles[i].getName().contains("3")) {
                    System.out.println("Usuário Nível 3");
                    JTable table = new JTable(dataTable3, columnNames3);
                    JScrollPane scrollPane = new JScrollPane(table);
                    frame.add(scrollPane);
                }

                // Criar frame com a tabela
                frame.pack();
                frame.setVisible(true);
            }
        }

        System.out.println("Cadastro não encontrado, inciando captura de imagens");


        // Iniciando variável para keyevent, 
        // conversor de frame para Mat e iniciando Camera.
    //     KeyEvent tecla = null;
    //     OpenCVFrameConverter.ToMat convertMat = new OpenCVFrameConverter.ToMat();
    //     OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
    //     camera.start();

    //     // Arquivo treinado para reconhecimento de faces.
    //     CascadeClassifier detectorFace = new CascadeClassifier("src\\recursos\\haarcascade_frontalface_alt.xml");

    //     CanvasFrame cFrame = new CanvasFrame("Preview", CanvasFrame.getDefaultGamma() / camera.getGamma());
    //     Frame frameCapturado = null;

    //     Mat imagemColorida = new Mat();

    //     // Tratamento da imagem capturada
    //     while ((frameCapturado = camera.grab()) != null) {
    //         imagemColorida = convertMat.convert(frameCapturado);
    //         Mat imagemCinza = new Mat();
    //         cvtColor(imagemColorida, imagemCinza, 10);
    //         RectVector facesDetectadas = new RectVector();

    //         detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 1, 0, new Size(150, 150),
    //                 new Size(500, 500));

    //         if (tecla == null) {
    //             tecla = cFrame.waitKey(5);
    //         }

    //         for (int i = 0; i < facesDetectadas.size(); i++) {
    //             Rect dadosFace = facesDetectadas.get(0);
    //             rectangle(imagemColorida, dadosFace, new Scalar(0, 0, 255, 0));
    //             Mat faceCapturada = new Mat(imagemCinza, dadosFace);
    //             resize(faceCapturada, faceCapturada, new Size(160, 160));

    //             if (tecla == null) {
    //                 tecla = cFrame.waitKey(5);
    //             }
    
    //             if (tecla != null) {
    //                 if (tecla.getKeyChar() == 'q') {
    //                     if (amostra <= numeroAmostras) {
    //                         // Salvando a imagem capturada
    //                         // cvtColor(faceCapturada, faceCapturada, 6);
    //                         System.out.println("Foto " + amostra + " capturada\n");
    //                         String arquivo = "src\\fotos\\pessoa." + nomePessoa + " - " + idAcesso + ".jpg";
    //                         imwrite(arquivo, faceCapturada);
    //                         amostra++;
    //                     }
    //                 }

    //                 tecla = null;
    //             }
    //         }

    //         if (tecla != null) {
    //             tecla = cFrame.waitKey(20);
    //         }


    //         if (cFrame.isVisible()) {
    //             cFrame.showImage(frameCapturado);
    //         }

    //         if (amostra > numeroAmostras) {
    //             break;
    //         }
    //     }
    //     cFrame.dispose();
    //     camera.stop();
    }
}
