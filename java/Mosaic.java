import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO; 
import javax.swing.*; 
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import net.coobird.thumbnailator.*;
import java.lang.Math.*;

class MyImages {
		public BufferedImage input_img;
		public int average_red;
		public int average_green;
		public int average_blue;

		static String path = "C:\\Users\\Donald\\Desktop\\Mosaic";

	}

public class Mosaic extends JPanel {

	
	//mosaic class//

	static ArrayList<MyImages> photo_db = new ArrayList<MyImages>();
	//change path
	static String path = "C:\\Users\\Donald\\Desktop\\Mosaic";

	static BufferedImage final_mosaic = null;

	File dirFile = new File(path);
	File []fileList = dirFile.listFiles();



	public static void main (String [] args) {
		Mosaic m = new Mosaic ();
		m.File_path(path);
		MyImages target_image = new MyImages();
		target_image.input_img = ImageIO.read(new File(path, "target.png"));
		m.create_mosaic(target_image, 40);
		m.save_image();

	}


	public void File_path (String path){

		for(File tempFile : fileList){
			MyImages img = new MyImages();
			if(tempFile.isFile()) {
				String tempPath = tempFile.getParent();
				String tempFileName = tempFile.getName();
				try{
					img.input_img = ImageIO.read(new File(tempPath + tempFileName));
				} catch (IOException e){

				}
				average_color_rgb(img);
				photo_db.add(img);
			}
		}

	}
	public void save_image(){
		if (final_mosaic == null){
			System.out.println("mosaic failed");
		}
		else{
			File f = new File("final_mosaic.jpg");
			ImageIO.write(final_mosaic, "JPEG", f);
		}
	}

	public void create_mosaic(MyImages img, int min_size){

		int image_min_size = min_size;
		final_mosaic = create_mosaic_helper(img, image_min_size, photo_db);
	}

	public static BufferedImage create_mosaic_helper(MyImages image, int min_size, ArrayList photo_db){
		//helper for create_mosaic
		int width = image.input_img.getWidth();
		int height = image.input_img.getHeight();

		if ((width < min_size) || (height < min_size)){
			BufferedImage best_match = find_best_match(photo_db, image);
			BufferedImage resized_best_match = resize_image(best_match, width, height);

			return resized_best_match;
		}
		else {
			divide_replace(image.input_img, photo_db, min_size);
		}

	}

	public static BufferedImage find_best_match(ArrayList photo_db, MyImages image){
		// find best match
		double best_match_value = Double.POSITIVE_INFINITY;
		BufferedImage best_match_image;
		average_color_rgb(image);

		for (MyImages samples : photo_db){
			double comparison_result = image_comparison(samples, image);
			if (comparison_result < best_match_value){
				best_match_value = comparison_result;
				best_match_image = samples.input_img;

			}
		}

		return best_match_image;
	}

	public static void average_color_rgb(MyImages image){
		// calculate average rgb value within the pixels
		int total_red = 0;
		int total_green = 0;
		int total_blue = 0;
		int pixel_height = image.input_img.getTileHeight();
		int pixel_width = image.input_img.getTileWidth();
		int total_pixel = pixel_height * pixel_width;

		for (int i=0; i < pixel_height; i++){
			for (int j=0; j < pixel_width; j++){
				Color image_color = new Color(image.input_img.getRGB(i, j));
					total_red += image_color.getRed();
					total_green += image_color.getGreen();
					total_blue += image_color.getBlue();
			}
		}
		image.average_red = total_red / total_pixel;
		image.average_green = total_green / total_pixel;
		image.average_blue = total_blue / total_pixel;
	}

	public static double image_comparison(MyImages image1, MyImages image2){
		//compare and return the distance of two images rgb values
		double red_diff = image1.average_red - image2.average_red;
		double green_diff = image1.average_green - image2.average_green;
		double blue_diff = image1.average_blue - image2.average_blue;

		double distance_result = sqrt( (red_diff * red_diff)+(green_diff * green_diff)+ (blue_diff * blue_diff));

		return distance_result;
	}

	public static BufferedImage resize_image(BufferedImage image, int width, int height){

		BufferedImage resized_image = Thumbnails.of(image).size(width, height).asBufferedImage();

		return resized_image;
	}

	public static MyImages divide_replace(BufferedImage image, ArrayList photo_db, int min_size){
		int width = image.getTileWidth();
		int height = image.getTileHeight();

		MyImages top_left_img = new MyImages();
		MyImages top_right_img = new MyImages();
		MyImages bottom_left_img = new MyImages();
		MyImages bottom_right_img = new MyImages();

		top_left_img.input_img = image.getSubimage(0, 0, width/2, height/2);
		top_right_img.input_img = image.getSubimage((width/2)+2, 0, width, height/2);
		bottom_left_img.input_img = image.getSubimage(0, (height/2) + 2, width/2, height);
		bottom_right_img.input_img = image.getSubimage((width/2)+2, (height/2) + 2, width, height);

		MyImages new_image = new MyImages();
		new_image.input_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics g = new_image.input_img.getGraphics();

		g.drawImage(create_mosaic_helper(top_left_img, min_size, photo_db), 0, 0, null);
		g.drawImage(create_mosaic_helper(top_right_img, min_size, photo_db), (width/2)+2, 0, null);
		g.drawImage(create_mosaic_helper(bottom_left_img, min_size, photo_db), 0, (height/2)+2, null);
		g.drawImage(create_mosaic_helper(bottom_right_img, min_size, photo_db),(width/2)+2, (height/2), null);


		return new_image;
		
	}

}