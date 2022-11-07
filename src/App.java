import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Scanner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.EigenFaceRecognizer;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.bytedeco.opencv.opencv_core.Rect;


import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;

import org.bytedeco.javacv.Frame;

public class App {
    public static void main(String args[]) throws InterruptedException, IOException {
        // Verificação das credenciais do usuário
        System.out.println("Digite seu Nome: ");
        Scanner cadastro = new Scanner(System.in);
        String nomePessoa = cadastro.nextLine();

        System.out.println("Digite seu Nível de Acesso: ");
        Scanner acessoNivel = new Scanner(System.in);
        int idAcesso = acessoNivel.nextInt();

        String fileName = "pessoa." + nomePessoa + " - " + idAcesso + ".jpg";
        Boolean userExists = false;

        String[] columnNames1 = {
            "Nome das Fazendas",
            "Atividade"
        };

        Object[][] dataTable1 = {
            {"Fazenda Nova Piratininga", "Pecuária"},
            {"Fazenda Roncador", "Agropecuária"},
            {"Fazenda São Marcelo", "Agropecuária"},
            {"Grupo SLC Agrícola|", "Agropecuária"},
            {"Grupo Bom Futuro ", "Pecuária"}
        };

        String[] columnNames2 = {
            "Nome das Fazendas",
            "Atividade",
            "Unidades",	
            "Localização da Sede"
        };

        Object[][] dataTable2 = {
            {"Fazenda Nova Piratininga", "Pecuária", 12,"São Miguel do Araguaia (GO)"},
            {"Fazenda Roncador", "Agropecuária", 6, "Querência (MT)"},
            {"Fazenda São Marcelo", "Agropecuária", 8, "Tangará da Serra e Juruena (MT)"},
            {"Grupo SLC Agrícola", "Agropecuária", 23, "Porto Alegre (RS)"},
            {"Grupo Bom Futuro ", "Pecuária", 33, "Porto Alegre (RS)"}
        };

        String[] columnNames3 = {
            "Nome das Fazendas",
            "Atividade",
            "Unidades",	
            "Localização da Sede",
            "Uso de Agrotóxico",
            "Receita Anual"
        };

        Object[][] dataTable3 = {
            {"Fazenda Nova Piratininga", "Pecuária", 12,"São Miguel do Araguaia (GO)", "Alto", "124 milhões"},
            {"Fazenda Roncador", "Agropecuária", 6, "Querência (MT)", "Médio", "87 milhões"},
            {"Fazenda São Marcelo", "Agropecuária", 8, "Tangará da Serra e Juruena (MT)", "Baixo", "23 milhões"},
            {"Grupo SLC Agrícola|", "Agropecuária", 23, "Porto Alegre (RS)", "Baixo", "245 milhões"},
            {"Grupo Bom Futuro ", "Pecuária", 33, "Porto Alegre (RS)", "Alto", "320 milhões"}
        };

        File folder = new File("src\\fotos\\");
        File[] listOfFiles = folder.listFiles();
        JFrame frame = new JFrame("Tabela de Fazendas");

        for (int i = 0; i < listOfFiles.length; i++) {
            // Verifica se o usuário já existe
            if (listOfFiles[i].getName().equals(fileName)) {
                System.out.println("Usuário já cadastrado!");
                userExists = true;
                // Verificação do Nível de Acesso
                if (listOfFiles[i].getName().contains("1")) {
                    if(ImageValidator(fileName)) {
                        System.out.println("Usuário Nível 1");
                        JTable table = new JTable(dataTable1, columnNames1);
                        JScrollPane scrollPane = new JScrollPane(table);
                        frame.add(scrollPane);
                    } else {
                        System.out.println("Foto não reconhecida!");
                    }
                } else if (listOfFiles[i].getName().contains("2")) {
                    if(ImageValidator(fileName)) {
                        System.out.println("Usuário Nível 2");
                        JTable table = new JTable(dataTable2, columnNames2);
                        JScrollPane scrollPane = new JScrollPane(table);
                        frame.add(scrollPane);
                    } else {
                        System.out.println("Foto não reconhecida!");
                    }
                } else if (listOfFiles[i].getName().contains("3")) {
                    if(ImageValidator(fileName)) {
                        System.out.println("Usuário Nível 3");
                        JTable table = new JTable(dataTable3, columnNames3);
                        JScrollPane scrollPane = new JScrollPane(table);
                        frame.add(scrollPane);
                    } else {
                        System.out.println("Foto não reconhecida!");
                    }
                }

                // Criar frame com a tabela
                frame.pack();
                frame.setVisible(true);
            }
        }

        // Se o usuário não existir, cria um novo
        if (!userExists) {
            System.out.println("Cadastro não encontrado, inciando captura de imagens");
            CaptureImage(nomePessoa, idAcesso);
        }
    }

    public static void CaptureImage(String nomePessoa, int idAcesso) throws FrameGrabber.Exception, InterruptedException {
        int numeroAmostras = 25;
        int amostra = 1;

        // Iniciando variável para keyevent, 
        // conversor de frame para Mat e iniciando Camera.
        KeyEvent tecla = null;
        OpenCVFrameConverter.ToMat convertMat = new OpenCVFrameConverter.ToMat();
        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
        camera.start();

        // Arquivo treinado para reconhecimento de faces.
        CascadeClassifier detectorFace = new CascadeClassifier("src\\recursos\\haarcascade_frontalface_alt.xml");

        CanvasFrame cFrame = new CanvasFrame("Preview", CanvasFrame.getDefaultGamma() / camera.getGamma());
        Frame frameCapturado = null;

        Mat imagemColorida = new Mat();

        // Tratamento da imagem capturada
        while ((frameCapturado = camera.grab()) != null) {
            imagemColorida = convertMat.convert(frameCapturado);
            Mat imagemCinza = new Mat();
            cvtColor(imagemColorida, imagemCinza, 10);
            RectVector facesDetectadas = new RectVector();

            detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 1, 0, new Size(150, 150),
                    new Size(500, 500));

            if (tecla == null) {
                tecla = cFrame.waitKey(5);
            }

            for (int i = 0; i < facesDetectadas.size(); i++) {
                Rect dadosFace = facesDetectadas.get(0);
                rectangle(imagemColorida, dadosFace, new Scalar(0, 0, 255, 0));
                Mat faceCapturada = new Mat(imagemCinza, dadosFace);
                resize(faceCapturada, faceCapturada, new Size(160, 160));

                if (tecla == null) {
                    tecla = cFrame.waitKey(5);
                }
    
                if (tecla != null) {
                    if (tecla.getKeyChar() == 'q') {
                        if (amostra <= numeroAmostras) {
                            // Salvando a imagem capturada
                            // cvtColor(faceCapturada, faceCapturada, 6);
                            System.out.println("Foto " + amostra + " capturada\n");
                            String arquivo = "src\\fotos\\pessoa." + nomePessoa + " - " + idAcesso + ".jpg";
                            imwrite(arquivo, faceCapturada);
                            amostra++;
                        }
                    }

                    tecla = null;
                }
            }

            if (tecla != null) {
                tecla = cFrame.waitKey(20);
            }


            if (cFrame.isVisible()) {
                cFrame.showImage(frameCapturado);
            }

            if (amostra > numeroAmostras) {
                break;
            }
        }

        cFrame.dispose();
        camera.stop();
    }

    public static boolean ImageValidator(String fileName) throws IOException, InterruptedException {
        // Carregando arquivo de treinamento
        File folder = new File("src\\fotos\\");
        File[] listOfFiles = folder.listFiles();
        boolean validUser = false;

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].getName().equals(fileName)) {
                String savedImage = listOfFiles[i].getPath();

                currentPhoto();

                BufferedImage savedUser = ImageIO.read(new File(savedImage));

                BufferedImage currentUser = ImageIO.read(new File("src\\fotos\\currentUser.jpg"));

                // Comparando saveUser com currentUser
                int count = 0;
                int width1 = savedUser.getWidth();
                int width2 = currentUser.getWidth();
                int height1 = savedUser.getHeight();
                int height2 = currentUser.getHeight();

                if ((width1 != width2) || (height1 != height2)) {
                    System.out.println("As imagens não são do mesmo tamanho");
                    validUser = false;
                } else {
                    for (int j = 0; j < width1; j++) {
                        for (int k = 0; k < height1; k++) {
                            if (savedUser.getRGB(j, k) != currentUser.getRGB(j, k)) {
                                count++;
                            }
                        }
                    }
                    System.out.println("The images are " + (count) + " pixels different");
                }

                if (count <= 1000) {
                    System.out.println("Foto reconhecida!");
                    validUser = true;
                } else {
                    System.out.println("Foto não reconhecida!");
                    validUser = false;
                }
            }
        }

        return validUser;
    }

    public static void currentPhoto() throws InterruptedException, Exception {
         // Iniciando variável para keyevent, 
        // conversor de frame para Mat e iniciando Camera.
        KeyEvent tecla = null;
        OpenCVFrameConverter.ToMat convertMat = new OpenCVFrameConverter.ToMat();
        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
        camera.start();
        int amostra = 0;

        // Arquivo treinado para reconhecimento de faces.
        CascadeClassifier detectorFace = new CascadeClassifier("src\\recursos\\haarcascade_frontalface_alt.xml");

        CanvasFrame cFrame = new CanvasFrame("Preview", CanvasFrame.getDefaultGamma() / camera.getGamma());
        Frame frameCapturado = null;

        Mat imagemColorida = new Mat();

        // Tratamento da imagem capturada
        while ((frameCapturado = camera.grab()) != null) {
            imagemColorida = convertMat.convert(frameCapturado);
            Mat imagemCinza = new Mat();
            cvtColor(imagemColorida, imagemCinza, 10);
            RectVector facesDetectadas = new RectVector();

            detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 1, 0, new Size(150, 150),
                    new Size(500, 500));

            if (tecla == null) {
                tecla = cFrame.waitKey(5);
            }

            for (int i = 0; i < facesDetectadas.size(); i++) {
                Rect dadosFace = facesDetectadas.get(0);
                rectangle(imagemColorida, dadosFace, new Scalar(0, 0, 255, 0));
                Mat faceCapturada = new Mat(imagemCinza, dadosFace);
                resize(faceCapturada, faceCapturada, new Size(160, 160));

                if (tecla == null) {
                    tecla = cFrame.waitKey(5);
                }
    
                if (tecla != null) {
                    if (tecla.getKeyChar() == 'q') {
                        // Salvando a imagem capturada
                        String arquivo = "src\\fotos\\currentUser.jpg";
                        imwrite(arquivo, faceCapturada);
                        amostra++;
                    }
                    tecla = null;
                }
            }

            if (cFrame.isVisible()) {
                cFrame.showImage(frameCapturado);
            }

            if (amostra > 0) {
                break;
            }
        }

        cFrame.dispose();
        camera.stop();
    }
}