package eal.service.format.eal;

import java.util.ArrayList;
import java.util.List;

public abstract class Item {

	public Integer ealid = null;
	public String title = null;
	public String description = null;
	public String question = null;
	

	
	public abstract int getPoints();

	public class Image {
		
		public String filename;
		public byte[] content;
		
		public Image(String filename, byte[] content) {
			super();
			this.filename = filename;
			this.content = content;
		}
	}
	
	private List<Image> images = new ArrayList<Image>();
	
	
	public void addImage (String filename, byte[] content) {
		images.add(new Image(filename, content));
	}
	
	public int getNumberOfImages () {
		return this.images.size();
	}
	
	public String getImageFilename (int index) {
		return this.images.get(index).filename;
	}

	public byte[] getImageContent (int index) {
		return this.images.get(index).content;
	}
	

	
//	public String replaceHtmlImgSrc (String html, List<String> keys, String prefix, int offset) {
//		
//		Pattern p = Pattern.compile("(<img[^>]+)src=[\"\\']([^\"]*)[\"\\']");
//		Matcher m = p.matcher(html);
//		StringBuffer sb = new StringBuffer();
//		while (m.find()) {
//
//			// if img is stored inline (src="data:image/png;base64,iVBOR....")  
//			if (m.group(2).substring(0, 5).equalsIgnoreCase("data:")) {
//				// TODO extract file; modify
//				m.appendReplacement(sb, m.group(1) + "src='" + m.group(2) + "'");
//				continue;
//			}
//			
//			// src is URL (contains dot) --> do nothing
//			int index = keys.indexOf(m.group(2));
//			if (index > -1) {
//				m.appendReplacement(sb, m.group(1) + "src='" + prefix + (index+offset) + "'");
//				continue;
//			}
//
//			// do not change
//			m.appendReplacement(sb, m.group(1) + "src='" + m.group(2) + "'");
//		}
//		m.appendTail(sb);
//		return sb.toString();
//		
//	}
//	
//	public int normalizeAttachments (String prefix, int offset) {
//		
//		List<String> listOfAllOldKeys = new ArrayList<String>(this.attachments.keySet());
//		Map<String, Attachment> attachmentsNew = new HashMap<String, Attachment>();
//		for (int index=0; index<listOfAllOldKeys.size(); index++) {
//			attachmentsNew.put(prefix + (index+offset), attachments.get(listOfAllOldKeys.get(index)));
//		}
//		attachments = attachmentsNew;
//		
//		description = replaceHtmlImgSrc(description, listOfAllOldKeys, prefix, offset);
//		question = replaceHtmlImgSrc(question, listOfAllOldKeys, prefix, offset);
//
//		return attachments.size();
//	}
//	
//	
//	public void setAttachmentFilename (String key, String filename) {
//		
//		Attachment a = attachments.get(key);
//		if (a==null) {
//			a = new Attachment();
//		}
//		a.filename = filename;
//		attachments.put(key, a);
//	}
//	
//	public void setAttachmentContent (String key, byte[] content) {
//		
//		Attachment a = attachments.get(key);
//		if (a==null) {
//			a = new Attachment();
//		}
//		a.content = content;
//		attachments.put(key, a);
//	}
//
//	public String[] getAttachmentKeys () {
//		return (String[]) attachments.keySet().toArray(new String[attachments.keySet().size()]);
//	}
//	
//	public String getAttachmentFilename (String key) {
//		return attachments.get(key).filename;
//	}
//	
//	public byte[] getAttachmentContent (String key) {
//		return attachments.get(key).content;
//	}

	
}
