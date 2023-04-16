package dk.easv;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImageViewerWindowController implements Initializable
{
    @FXML
    private Button btnLoad;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnStop;
    @FXML
    private ChoiceBox<Integer> chbSeconds;
    @FXML
    private Label lblImageName;
    @FXML
    private Label lblImagePixelColors;
    @FXML
    private Parent root;
    @FXML
    private ImageView imageView;


    private final List<Image> images = new ArrayList<>();
    private int currentImageIndex = 0;

    private Thread slideShowThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disableSlideShowButtons(true);
        initializeChoiceBoxSecondsPicker();
    }

    @FXML
    private void handleBtnLoadAction()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());

        if (!files.isEmpty())
        {
            files.forEach((File f) ->
            {
                images.add(new Image(f.toURI().toString()));
            });
            displayImage();

            if (!images.isEmpty()) {
                disableManualSlideShowSwitchButtons(false);
                switchDisableSlideShowStartStop(false);
            }
        }
    }

    @FXML
    private void handleBtnPreviousAction()
    {
        if (!images.isEmpty())
        {
            currentImageIndex =
                    (currentImageIndex - 1 + images.size()) % images.size();
            displayImage();
        }
    }

    @FXML
    private void handleBtnNextAction()
    {
        if (!images.isEmpty())
        {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            displayImage();
        }
    }

    @FXML
    private void handleBtnStartAction(ActionEvent actionEvent) {
        btnLoad.setDisable(true);
        switchDisableSlideShowStartStop(true);
        disableManualSlideShowSwitchButtons(true);

        Task<Image> slideShowTask = new SlideShowTask<>(images, currentImageIndex, chbSeconds.getValue());

        setSlideShowTaskValueListener(slideShowTask);

        slideShowThread = new Thread(slideShowTask);
        slideShowThread.setDaemon(true);

        slideShowThread.start();
    }

    @FXML
    private void handleBtnStopAction(ActionEvent actionEvent) {
        btnLoad.setDisable(false);
        switchDisableSlideShowStartStop(false);
        disableManualSlideShowSwitchButtons(false);

        if (!slideShowThread.isInterrupted()) slideShowThread.interrupt();
    }

    private void displayImage()
    {
        if (!images.isEmpty())
        {
            setPixelColors(images.get(currentImageIndex));

            imageView.setImage(images.get(currentImageIndex));
            showImageName();
        }
    }

    private void showImageName() {
        if (!images.isEmpty()) {

            File imageAsFile = new File(images.get(currentImageIndex).getUrl());
            String imageFileName = imageAsFile.getName();

            lblImageName.setText(imageFileName);
        }
    }

    private void setPixelColors(Image image) {
        Task<Image> pixelColorReaderTask = new PixelColorReaderTask(image);

        Thread pixelColorThread = new Thread(pixelColorReaderTask);
        pixelColorThread.setDaemon(true);

        setPixelColorListener(pixelColorReaderTask);

        pixelColorThread.start();
    }

    private void setPixelColorListener(Task<Image> pixelColorReaderTask) {
        if (pixelColorReaderTask == null) return;

        pixelColorReaderTask.messageProperty().addListener((observable, oldValue, newValue) -> {

            Platform.runLater(() -> {

                lblImagePixelColors.setText(newValue);
                pixelColorReaderTask.cancel();
            });
        });
    }

    private void setSlideShowTaskValueListener(Task<Image> slideShowTask) {
        if (slideShowTask == null) return;

        slideShowTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleBtnNextAction();
        });
    }

    private void switchDisableSlideShowStartStop(boolean disableStart) {
        btnStart.setDisable(disableStart);
        btnStop.setDisable(!disableStart);
    }

    private void disableManualSlideShowSwitchButtons(boolean disable) {
        btnPrevious.setDisable(disable);
        btnNext.setDisable(disable);
    }

    private void disableSlideShowButtons(boolean disable) {
        disableManualSlideShowSwitchButtons(disable);
        btnStart.setDisable(disable);
        btnStop.setDisable(disable);
    }

    private void initializeChoiceBoxSecondsPicker() {
        List<Integer> secondsToPick = new ArrayList<>();
        secondsToPick.add(1);
        secondsToPick.add(2);
        secondsToPick.add(3);
        secondsToPick.add(4);
        secondsToPick.add(5);

        chbSeconds.getItems().setAll(secondsToPick);
        chbSeconds.setValue(1);
    }
}