package me.wener.showea.images;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.connectedcomponent.GreyscaleConnectedComponentLabeler;
import org.openimaj.image.dataset.FlickrImageDataset;
import org.openimaj.image.pixel.ConnectedComponent;
import org.openimaj.image.pixel.statistics.HistogramModel;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.geometry.shape.Ellipse;
import org.openimaj.math.statistics.distribution.MultidimensionalHistogram;
import org.openimaj.ml.clustering.FloatCentroidsResult;
import org.openimaj.ml.clustering.assignment.HardAssigner;
import org.openimaj.ml.clustering.kmeans.FloatKMeans;
import org.openimaj.util.api.auth.DefaultTokenFactory;
import org.openimaj.util.api.auth.common.FlickrAPIToken;

public class PlayImages
{
    @Test
    public void first() throws IOException, InterruptedException
    {
        //http://www.openimaj.org/tutorial/processing-your-first-image.html
        MBFImage image = ImageUtilities.readMBF(ClassLoader.getSystemResourceAsStream("file.jpg"));
        System.out.println(image.colourSpace);
//        DisplayUtilities.display(image);
//        DisplayUtilities.display(image.getBand(0), "Example");

        MBFImage clone = image.clone();
        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                clone.getBand(1).pixels[y][x] = 0;
                clone.getBand(2).pixels[y][x] = 0;
            }
        }
        DisplayUtilities.display(clone, "Example");
        clone.getBand(1).fill(0f);
        clone.getBand(2).fill(0f);
        image.processInplace(new CannyEdgeDetector());
        DisplayUtilities.display(image);

        image.drawShapeFilled(new Ellipse(700f, 450f, 20f, 10f, 0f), RGBColour.WHITE);
        image.drawShapeFilled(new Ellipse(650f, 425f, 25f, 12f, 0f), RGBColour.WHITE);
        image.drawShapeFilled(new Ellipse(600f, 380f, 30f, 15f, 0f), RGBColour.WHITE);
        image.drawShapeFilled(new Ellipse(500f, 300f, 100f, 70f, 0f), RGBColour.WHITE);
        image.drawText("OpenIMAJ is", 425, 300, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
        image.drawText("Awesome", 425, 330, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);
        DisplayUtilities.display(image);
        Thread.sleep(200000);
    }

    @Test
    public void clustering() throws IOException, InterruptedException
    {
        MBFImage input = ImageUtilities.readMBF(ClassLoader.getSystemResourceAsStream("file.jpg"));
        input = ColourSpace.convert(input, ColourSpace.CIE_Lab);
        FloatKMeans cluster = FloatKMeans.createExact(2);
        float[][] imageData = input.getPixelVectorNative(new float[input.getWidth() * input.getHeight()][3]);
        FloatCentroidsResult result = cluster.cluster(imageData);
        float[][] centroids = result.centroids;
        for (float[] fs : centroids)
        {
            System.out.println(Arrays.toString(fs));
        }
        HardAssigner<float[], ?, ?> assigner = result.defaultHardAssigner();
        for (int y = 0; y < input.getHeight(); y++)
        {
            for (int x = 0; x < input.getWidth(); x++)
            {
                float[] pixel = input.getPixelNative(x, y);
                int centroid = assigner.assign(pixel);
                input.setPixelNative(x, y, centroids[centroid]);
            }
        }
        input = ColourSpace.convert(input, ColourSpace.RGB);
        DisplayUtilities.display(input);
        GreyscaleConnectedComponentLabeler labeler = new GreyscaleConnectedComponentLabeler();
        List<ConnectedComponent> components = labeler.findComponents(input.flatten());
        int i = 0;
        for (ConnectedComponent comp : components)
        {
            if (comp.calculateArea() < 50)
                continue;
            input.drawText("Point:" + (i++), comp.calculateCentroidPixel(), HersheyFont.TIMES_MEDIUM, 20);
        }
        DisplayUtilities.display(input);

        Thread.sleep(200000);
    }

    @Test
    public void global() throws IOException
    {
        URL[] imageURLs = new URL[]{
                new URL("http://users.ecs.soton.ac.uk/dpd/projects/openimaj/tutorial/hist1.jpg"),
                new URL("http://users.ecs.soton.ac.uk/dpd/projects/openimaj/tutorial/hist2.jpg"),
                new URL("http://users.ecs.soton.ac.uk/dpd/projects/openimaj/tutorial/hist3.jpg")
        };

        List<MultidimensionalHistogram> histograms = new ArrayList<MultidimensionalHistogram>();
        HistogramModel model = new HistogramModel(4, 4, 4);

        for (URL u : imageURLs)
        {
            model.estimateModel(ImageUtilities.readMBF(u));
            histograms.add(model.histogram.clone());
        }

        for (int i = 0; i < histograms.size(); i++)
        {
            for (int j = i; j < histograms.size(); j++)
            {
                double distance = histograms.get(i).compare(histograms.get(j), DoubleFVComparison.EUCLIDEAN);
                System.out.printf("%s VS %s -> %s\n", i, j, distance);
            }
        }
    }

    @Test
    public void database() throws Exception
    {
        VFSListDataset<FImage> images = new VFSListDataset<FImage>("/Users/wener/gits/note/ignored/us/", ImageUtilities.FIMAGE_READER);
        System.out.println(images.size());
        DisplayUtilities.display("All", images);

        if (false)
        {
            VFSListDataset<FImage> faces = new VFSListDataset<FImage>("zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
            DisplayUtilities.display("ATT faces", faces);

            VFSGroupDataset<FImage> groupedFaces = new VFSGroupDataset<FImage>("zip:http://datasets.openimaj.org/att_faces.zip", ImageUtilities.FIMAGE_READER);
            for (final Map.Entry<String, VFSListDataset<FImage>> entry : groupedFaces.entrySet())
            {
                DisplayUtilities.display(entry.getKey(), entry.getValue());
            }
        }

        if (false)
        {
            FlickrAPIToken flickrToken = DefaultTokenFactory.get(FlickrAPIToken.class);
            FlickrImageDataset<FImage> cats = FlickrImageDataset
                    .create(ImageUtilities.FIMAGE_READER, flickrToken, "cat", 10);
            DisplayUtilities.display("Cats", cats);
        }

        Thread.sleep(200000);
    }
}
