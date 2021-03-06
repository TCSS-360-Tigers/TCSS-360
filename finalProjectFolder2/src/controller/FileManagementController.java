package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import model.Category;
import model.UserProfile;
import objectInterface.FileOperationInterface;
import utilities.Constants;

/**
 * This class works as a driver to provide the method to communicate between the
 * service layer with the file system. This class will provide methods such as
 * get file, get category, add new file, add new category,... In other words,
 * all of the communication with the file systems must go through this class.
 * 
 * @author Anh Tran
 */
public class FileManagementController implements FileOperationInterface {

	/**
	 * Veryfing the authetioncation.
	 * 
	 * @author Anh Tran
	 */
	public boolean verifyAuthentication(String userName, String password) {
		List<UserProfile> lstOfUser = getAllUser();
		for (UserProfile user : lstOfUser) {
			if (user.getUserName().equalsIgnoreCase(userName) && user.getPassword().equalsIgnoreCase(password)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Importing user profile.
	 * 
	 * @author Anh Tran
	 */
	@Override
	public void importSetting(UserProfile userProfile) {
		BufferedWriter bw = null;
		try {
			List<UserProfile> lstOfUser = getAllUser();
			bw = new BufferedWriter(new FileWriter(Constants.USER_INFO_PATH));
			for (UserProfile user : lstOfUser) {
				if (user.getUserName().equalsIgnoreCase(userProfile.getUserName())) {
					bw.write(userProfile.toString());
				} else {
					bw.write(user.toString());
				}
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Get all the user profiles and push into the list.
	 * 
	 * @author Anh Tran
	 */
	private List<UserProfile> getAllUser() {
		List<UserProfile> lstOfUser = new ArrayList<>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(Constants.USER_INFO_PATH));
			String row = br.readLine();
			while (row != null) {
				String data[] = row.split(",");
				UserProfile user = new UserProfile(data[0],data[1], data[2],data[3]);
				/*
				 * user.setUserName(data[0]); user.setPassword(data[1]); user.setName(data[2]);
				 * user.setEmail(data[3]);
				 */
				lstOfUser.add(user);
				row = br.readLine();
			}
			br.close();
		} catch (Exception e) {
		}
		return lstOfUser;
	}

	/***
	 * Get the User profile based on their username.
	 * 
	 * @author Anh Tran
	 */
	@Override
	public UserProfile getUserBasedOnUserName(String userName) {
		List<UserProfile> lstOfUser;
		try {
			lstOfUser = getAllUser();
			for (UserProfile user : lstOfUser) {
				if (user.getUserName().equalsIgnoreCase(userName)) {
					return user;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	/***
	 * Get list of files by folder (categories).
	 * 
	 * @author Anh Tran
	 */
	@Override
	public File[] getListOfFileByFolder(String path) {
		return new File(path).listFiles();
	}

	/***
	 * Add a choosen file from user into the file system under the specific
	 * category.
	 * 
	 * @author Anh Tran
	 */
	@Override
	public void addFile(model.File file) {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(new File(file.getPath()));
			os = new FileOutputStream(
					new File("FileFolder\\" + file.getCategory().getCategoryName() + "\\" + file.getFileName()));
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = is.read(buf)) > 0) {
				os.write(buf, 0, bytesRead);
			}
		} catch (Exception e) {
		} finally {
			try {
				is.close();
				os.close();
			} catch (IOException e) {
			}
		}

	}

	/***
	 * Get List of categories.
	 * 
	 * @author Anh Tran
	 */
	public Category[] getCategories() {
		File root = new File("FileFolder\\");
		Category categoriesLst[] = new Category[root.listFiles().length];
		int i = 0;
		for (File f : root.listFiles()) {
			categoriesLst[i++] = new Category(f.getName());
		}
		return categoriesLst;
	}

	@Override
	public void addCategory(String category) {
		File root = new File("FileFolder\\" + category);
		root.mkdirs();
	}

}
