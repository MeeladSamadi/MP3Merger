import javax.swing.*;  // Imports the Swing package for creating GUI components
import java.io.*;      // Imports I/O classes for file handling
import java.util.ArrayList;   // Imports the ArrayList class
import java.util.Arrays;      // Imports the Arrays utility class
import java.util.Collections; // Imports the Collections class for shuffling the file list
import java.util.List;        // Imports the List interface

public class MP3Merger {

    // Main method where the program execution begins
    public static void main(String[] args) throws Exception {
        // Create a file chooser dialog to select multiple MP3 files
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);  // Enable selecting multiple files
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // Restrict selection to files only
        fileChooser.setDialogTitle("Select MP3 Files to Merge");  // Set the dialog title

        // Show the file chooser dialog and store the user's action (Approve/Cancel)
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            // If the user cancels or doesn't approve, exit the program
            System.out.println("No files selected. Exiting.");
            return;
        }

        // Store the selected files in an array
        File[] selectedFiles = fileChooser.getSelectedFiles();
        if (selectedFiles.length == 0) {
            // If no files were selected, exit the program
            System.out.println("No MP3 files selected. Exiting.");
            return;
        }

        // Validate that all selected files have an MP3 extension
        for (File file : selectedFiles) {
            if (!file.getName().toLowerCase().endsWith(".mp3")) {
                // If a non-MP3 file is found, show an error message and exit
                System.out.println("Invalid file format: " + file.getName() + ". Only MP3 files are allowed.");
                return;
            }
        }

        // Ask the user if they want to randomize the order of the selected files
        int randomizeOption = JOptionPane.showConfirmDialog(null, "Do you want to randomize the order of the files?", "Randomize Order", JOptionPane.YES_NO_OPTION);
        if (randomizeOption == JOptionPane.YES_OPTION) {
            // If the user selects "Yes", shuffle the order of the selected files
            List<File> fileList = Arrays.asList(selectedFiles);  // Convert the array to a list for shuffling
            Collections.shuffle(fileList);  // Shuffle the list randomly
            selectedFiles = fileList.toArray(new File[0]);  // Convert the shuffled list back to an array
        }

        // Create a file chooser dialog to select the output file (merged MP3)
        JFileChooser saveFileChooser = new JFileChooser();
        saveFileChooser.setDialogTitle("Save Merged MP3 File As");  // Set the dialog title for saving
        saveFileChooser.setSelectedFile(new File("merged.mp3"));  // Set a default file name for the output

        // Show the save dialog and store the user's action (Approve/Cancel)
        int saveFileOption = saveFileChooser.showSaveDialog(null);
        if (saveFileOption != JFileChooser.APPROVE_OPTION) {
            // If the user cancels or doesn't approve, exit the program
            System.out.println("Output file not selected. Exiting.");
            return;
        }

        // Get the selected file for output (where the merged MP3 will be saved)
        File outputFile = saveFileChooser.getSelectedFile();
        if (outputFile.exists()) {
            // If the output file already exists, ask for confirmation to overwrite it
            int overwriteOption = JOptionPane.showConfirmDialog(null, "File already exists. Overwrite?", "Overwrite Confirmation", JOptionPane.YES_NO_OPTION);
            if (overwriteOption != JOptionPane.YES_OPTION) {
                // If the user chooses not to overwrite, exit the program
                System.out.println("Operation cancelled. Exiting.");
                return;
            }
        }

        // Call the method to merge the selected MP3 files into the output file
        mergeMP3Files(selectedFiles, outputFile);

        // Notify the user that the files have been successfully merged
        System.out.println("Files merged successfully.");
    }

    // Method to merge the selected MP3 files into a single output file
    public static void mergeMP3Files(File[] mp3Files, File output) throws Exception {
        try (
            // Create an output stream to write to the output file
            FileOutputStream fos = new FileOutputStream(output);
            BufferedOutputStream mergingStream = new BufferedOutputStream(fos)
        ) {

            // Loop through each MP3 file in the list
            for (File mp3File : mp3Files) {
                // Open each MP3 file for reading
                try (
                    FileInputStream fis = new FileInputStream(mp3File);
                    BufferedInputStream fileStream = new BufferedInputStream(fis)
                ) {

                    byte[] buffer = new byte[1024];  // Create a buffer to store bytes temporarily
                    int bytesRead;

                    // Read the MP3 file and write its contents into the output stream
                    while ((bytesRead = fileStream.read(buffer)) != -1) {
                        mergingStream.write(buffer, 0, bytesRead);  // Write the buffer to the output file
                    }
                }
            }
        } catch (IOException e) {
            // Handle any I/O errors that occur during the merging process
            e.printStackTrace();
        }
    }
}
