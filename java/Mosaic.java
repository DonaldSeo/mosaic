import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO; 
import javax.swing.*; 
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.image.*;
import java.io.File;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Iterator;

class MyImages {
		public BufferedImage input_img=null;
		public int average_red;
		public int average_green;
		public int average_blue;

		static String path = "C:/Users/Donald/Desktop/Mosaic/image/";

		public MyImages(BufferedImage img) {
			input_img = img;
		}
		public MyImages(){
			input_img = null;
			average_red = 0;
			average_green = 0;
			average_blue = 0;
		}
		public void setImage(BufferedImage img){

			this.input_img = img;

		}

	}

public class Mosaic extends JPanel {

	
	//mosaic class//

	static ArrayList<MyImages> photo_db = new ArrayList<MyImages>();
	//change path
	static String path = "C:/Users/Donald/Desktop/Mosaic/image/";

	static BufferedImage final_mosaic = null;

	File dirFile = new File(path);
	File []fileList = dirFile.listFiles();



	public static void main (String [] args) throws Exception{
		System.out.println("here 0");

		Mosaic m = new Mosaic ();
		MyImages target_image = new MyImages();
		m.File_path(path);
		try{
			target_image.input_img = ImageIO.read(new File(path+"target.PNG"));
		} catch (IOException e){
			e.printStackTrace();
		}
		System.out.println("here 1");
		m.create_mosaic(target_image, 10);
		m.save_image();

	}


	public void File_path (String path){

		for(File tempFile : fileList){
			MyImages img = new MyImages();
			if(tempFile.isFile()) {
				String tempPath = tempFile.getParent();
				String tempFileName = tempFile.getName();
				File imagefile = new File(tempPath+"/"+tempFileName);
				try{
					BufferedImage input_reading_img = ImageIO.read(imagefile);
					img.setImage(input_reading_img);
				} catch (IOException e){
					System.out.println("exception entered?");
				}
				if (img == null) {
					System.out.println("null here");
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
			try{
				ImageIO.write(final_mosaic, "JPEG", f);		
			} catch (IOException e){

			}
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
			System.out.println("min size now finally matched");
			BufferedImage best_match = find_best_match(photo_db, image);
			BufferedImage resized_best_match = resize_image(best_match, width, height);

			return resized_best_match;
			
		} else {
			System.out.println("min size not matched");
			return divide_replace(image.input_img, photo_db, min_size);
		}

	}

	public static BufferedImage find_best_match(ArrayList photo_db, MyImages image){
		// find best match
		double best_match_value = Double.POSITIVE_INFINITY;

		BufferedImage best_match_image = null;
		average_color_rgb(image);


		@SuppressWarnings("unchecked")
		Iterator<MyImages> i = photo_db.iterator();

		while (i.hasNext()){
			MyImages samples = i.next();

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

		for (int i=0; i < pixel_width; i++){
			for (int j=0; j < pixel_height; j++){
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

		double distance_result = Math.sqrt( (red_diff * red_diff)+(green_diff * green_diff)+ (blue_diff * blue_diff));

		return distance_result;
	}

	public static BufferedImage resize_image(BufferedImage image, int width, int height){

		BufferedImage resized_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = resized_image.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();		

		return resized_image;
	}

	public static BufferedImage divide_replace(BufferedImage image, ArrayList photo_db, int min_size){
		int width = image.getTileWidth();
		int height = image.getTileHeight();


	
		MyImages top_left_img = new MyImages();
		MyImages top_right_img = new MyImages();
		MyImages bottom_left_img = new MyImages();
		MyImages bottom_right_img = new MyImages(); 

		BufferedImage top_left = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage top_right = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage bottom_left = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage right_left = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		top_left_img.input_img = image.getSubimage(0, 0, width/2, height/2);
		top_right_img.input_img = image.getSubimage(width/2, 0, width/2, height/2);
		bottom_left_img.input_img = image.getSubimage(0, height/2, width/2, height/2);
		bottom_right_img.input_img = image.getSubimage(width/2, height/2, width/2, height/2);
		System.out.println("image width is");
		System.out.println(width);
		System.out.println("image height is");
		System.out.println(height);

		System.out.println("top_left width is");
		System.out.println(top_left_img.input_img.getTileWidth());
		System.out.println("top_left height is");
		System.out.println(top_left_img.input_img.getTileHeight());



		MyImages new_image = new MyImages();
		new_image.input_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics g = new_image.input_img.getGraphics();

		g.drawImage(create_mosaic_helper(top_left_img, min_size, photo_db), 0, 0, null);
		g.drawImage(create_mosaic_helper(top_right_img, min_size, photo_db), width/2, 0, null);
		g.drawImage(create_mosaic_helper(bottom_left_img, min_size, photo_db), 0, height/2, null);
		g.drawImage(create_mosaic_helper(bottom_right_img, min_size, photo_db),width/2, height/2, null);


		return new_image.input_img;
		
	}

}