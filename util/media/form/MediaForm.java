package util.media.form;

import org.apache.struts.action.ActionForm;

public class MediaForm extends ActionForm{
	private long id;
	private long forId;
	private String forType;
	private String name;
	private String slug;
	private String extension;
	private String type;
	private String url;
	private String path;
	private String status;
	public long getId() {
		return id;
	}
	public void setId(long p_id) {
		id = p_id;
	}
	public long getForId() {
		return forId;
	}
	public void setForId(long p_forId) {
		forId = p_forId;
	}
	public String getForType() {
		return forType;
	}
	public void setForType(String p_forType) {
		forType = p_forType;
	}
	public String getName() {
		return name;
	}
	public void setName(String p_name) {
		name = p_name;
	}
	public String getSlug() {
		return slug;
	}
	public void setSlug(String p_slug) {
		slug = p_slug;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String p_extension) {
		extension = p_extension;
	}
	public String getType() {
		return type;
	}
	public void setType(String p_type) {
		type = p_type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String p_url) {
		url = p_url;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String p_path) {
		path = p_path;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String p_status) {
		status = p_status;
	}
	@Override
	public String toString() {
		return "MediaForm [id=" + id + ", forId=" + forId + ", forType=" + forType + ", name=" + name + ", slug=" + slug
				+ ", extension=" + extension + ", type=" + type + ", url=" + url + ", path=" + path + ", status="
				+ status + "]";
	}
	
}
