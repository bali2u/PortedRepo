
//Multiple face detection and recognition in real time
//Using EmguCV cross platform .Net wrapper to the Intel OpenCV image processing library for C#.Net
//Writed by Sergio Andr?s Guit?rrez Rojas
//"Serg3ant" for the delveloper comunity
// Sergiogut1805@hotmail.com
//Regards from Bucaramanga-Colombia ;)

package MultiFaceRec;

// ********* THIS CODE IS AUTO PORTED FROM C# TO JAVA USING CODEPORTING.COM TECHNOLOGY *********

import java.util.ArrayList;
import com.codeporting.csharp2java.System.IO.File;
import com.codeporting.csharp2java.System.msString;
import com.codeporting.csharp2java.System.Convert;
import com.codeporting.csharp2java.System.EventArgs;
import com.codeporting.csharp2java.System.Drawing.Size;
import com.codeporting.csharp2java.System.Drawing.Color;
import com.codeporting.csharp2java.System.Drawing.Point;


public partial class FrmPrincipal extends Form
{
    //Declararation of all variables, vectors and haarcascades
    private Image<Bgr, Byte> currentFrame;
	private Image<Bgr, Byte> currentFrame1;
	private Image<Bgr, Byte> currentFrame2;

    private Capture grabber;
    private HaarCascade face;
    private HaarCascade eye;
    private MCvFont font = new MCvFont(FONT.CV_FONT_HERSHEY_TRIPLEX, 0.5d, 0.5d);
    private Image<Gray, byte> result, TrainedFace = null;
    private Image<Gray, byte> gray = null;
    private ArrayList<Image<Gray, byte>> trainingImages = new ArrayList<Image<Gray, byte>>();
    private ArrayList<String> labels= new ArrayList<String>();
    private ArrayList<String> NamePersons = new ArrayList<String>();
    private int ContTrain, NumLabels, t;
    private String name, names = null;


    public FrmPrincipal()
    {
        InitializeComponent();
        //Load haarcascades for face detection
        face = new HaarCascade("haarcascade_frontalface_default.xml");
        eye = new HaarCascade("haarcascade_eye.xml");
        try
        {
            //Load of previus trainned faces and labels for each image
            String Labelsinfo = File.ReadAllText(Application.StartupPath + "/TrainedFaces/TrainedLabels.txt");
            String[] Labels = msString.split(Labelsinfo, '%');
            NumLabels = Convert.toInt16(Labels[0]);
            ContTrain = NumLabels;
            String LoadFaces;

            for (int tf = 1; tf < NumLabels+1; tf++)
            {
                LoadFaces = com.codeporting.csharp2java.System.msString.concat("face",  tf,  ".bmp");
                trainingImages.Add(new Image<Gray, byte>(Application.StartupPath + "/TrainedFaces/" + LoadFaces));
                labels.add(Labels[tf]);
            }
        
        }
        catch(RuntimeException e)
        {
            //MessageBox.Show(e.ToString());
            MessageBox.Show("Nothing in binary database, please add at least a face", "Triained faces load", MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
        }

    }


    private void button1_Click(Object sender, EventArgs e)
    {
        //Initialize the capture device
        grabber = new Capture();
        grabber.QueryFrame();
        //Initialize the FrameGraber event
        Application.Idle += new EventHandler() {
public void invoke() {
frameGrabber();
}};
        button1.Enabled = false;
    }


    private void button2_Click(Object sender, com.codeporting.csharp2java.System.EventArgs e)
    {
        try
        {
            //Trained face counter
            ContTrain = ContTrain + 1;

            //Get a gray frame from capture device
            gray = grabber.QueryGrayFrame().Resize(320, 240, Emgu.CV.CvEnum.INTER.CV_INTER_CUBIC);

            //Face Detector
            MCvAvgComp[][] facesDetected = gray.DetectHaarCascade(
            face,
            1.2,
            10,
            Emgu.CV.CvEnum.HAAR_DETECTION_TYPE.DO_CANNY_PRUNING,
            new Size(20, 20));

            //Action for each element detected
            for (MCvAvgComp f : facesDetected[0])
            {
                TrainedFace = currentFrame.Copy(f.rect).Convert<Gray,Byte>();
                break;
            }

            //resize face detected image for force to compare the same size with the 
            //test image with cubic interpolation type method
            TrainedFace = result.Resize(100, 100, Emgu.CV.CvEnum.INTER.CV_INTER_CUBIC);
            trainingImages.add(TrainedFace);
            labels.Add(textBox1.Text);

            //Show face added in gray scale
            imageBox1.Image = TrainedFace;

            //Write the number of triained faces in a file text for further load
            File.WriteAllText(Application.StartupPath + "/TrainedFaces/TrainedLabels.txt", com.codeporting.csharp2java.System.msString.concat(Integer.toString(trainingImages.toArray().length),  "%"));

            //Write the labels of triained faces in a file text for further load
            for (int i = 1; i < trainingImages.toArray().length + 1; i++)
            {
                trainingImages.toArray()[i - 1].Save(Application.StartupPath + "/TrainedFaces/face" + i + ".bmp");
                File.AppendAllText(Application.StartupPath + "/TrainedFaces/TrainedLabels.txt", com.codeporting.csharp2java.System.msString.concat(labels.toArray()[i - 1],  "%"));
            }

            MessageBox.Show(textBox1.Text + "?s face detected and added :)", "Training OK", MessageBoxButtons.OK, MessageBoxIcon.Information);
        }
        catch(Exception e)
        {
            MessageBox.Show("Enable the face detection first", "Training Fail", MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
        }
    }


    private void frameGrabber(Object sender, EventArgs e)
    {
        label3.Text = "0";
        //label4.Text = "";
        NamePersons.add("");


        //Get the current frame form capture device
        currentFrame = grabber.QueryFrame().Resize(320, 240, Emgu.CV.CvEnum.INTER.CV_INTER_CUBIC);

                //Convert it to Grayscale
                gray = currentFrame.Convert<Gray,Byte>();

                //Face Detector
                MCvAvgComp[][] facesDetected = gray.DetectHaarCascade(
              face,
              1.2,
              10,
              Emgu.CV.CvEnum.HAAR_DETECTION_TYPE.DO_CANNY_PRUNING,
              new Size(20, 20));

                //Action for each element detected
                for (MCvAvgComp f : facesDetected[0])
                {
                    t = t + 1;
                    result = currentFrame.Copy(f.rect).Convert<Gray,Byte>().Resize(100, 100, Emgu.CV.CvEnum.INTER.CV_INTER_CUBIC);
                    //draw the face detected in the 0th (gray) channel with blue color
                    currentFrame.Draw(f.rect, new Bgr(Color.Red.Clone()), 2);


                    if (trainingImages.toArray().length != 0)
                    {
                        //TermCriteria for face recognition with numbers of trained images like maxIteration
                    MCvTermCriteria termCrit = new MCvTermCriteria(ContTrain, 0.001);

                    //Eigen face recognizer
                    <unknown>[] referenceToTermCrit = { termCrit };
                    EigenObjectRecognizer recognizer = new EigenObjectRecognizer(
                       trainingImages.toArray(),
                       labels.toArray(),
                       3000,
                       /*ref*/ referenceToTermCrit);
                    termCrit = referenceToTermCrit[0];

                    name = recognizer.Recognize(result);

                        //Draw the label for each face detected and recognized
                    <unknown>[] referenceToFont = { font };
                    currentFrame.Draw(name, /*ref*/ referenceToFont, new Point(f.rect.X - 2, f.rect.Y - 2), new Bgr(Color.LightGreen.Clone()));
                    font = referenceToFont[0];

                    }

                        NamePersons.set(t-1, name);
                        NamePersons.add("");


                    //Set the number of faces detected on the scene
                    label3.Text = facesDetected[0].Length.ToString();
                   
                    /*
                        //Set the region of interest on the faces
                        
                        gray.ROI = f.rect;
                        MCvAvgComp[][] eyesDetected = gray.DetectHaarCascade(
                           eye,
                           1.1,
                           10,
                           Emgu.CV.CvEnum.HAAR_DETECTION_TYPE.DO_CANNY_PRUNING,
                           new Size(20, 20));
                        gray.ROI = Rectangle.Empty;

                        foreach (MCvAvgComp ey in eyesDetected[0])
                        {
                            Rectangle eyeRect = ey.rect;
                            eyeRect.Offset(f.rect.X, f.rect.Y);
                            currentFrame.Draw(eyeRect, new Bgr(Color.Blue), 2);
                        }
                         */

                }
                    t = 0;

                    //Names concatenation of persons recognized
                for (int nnn = 0; nnn < facesDetected[0].Length; nnn++)
                {
                    names = com.codeporting.csharp2java.System.msString.concat(names,  NamePersons.get(nnn),  ", ");
                }
                //Show the faces procesed and recognized
                imageBoxFrameGrabber.Image = currentFrame;
                label4.Text = names;
                names = "";
                //Clear the list(vector) of names
                NamePersons.clear();

            }

}

