package code.barbot;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import modeles.Branch;
import modeles.Category;
import modeles.Constants;
import modeles.Project;
public class Parseur {

	private String login;
	private String password;
	private  String cookie=null;
	private Project project;
	private ArrayList<Project> projectList;
	public enum Level{PROJECT,CATEGORY,BRANCH}

	public  Level ParseurLevel=Level.PROJECT;
	public Parseur(String login, String password) {
		//Vérification de certificat
		if(setCertificate() ) {
			//vérifier la connexion et obtenir les cookies (afficher la page du formulaire de connexion)
			if(loadFormPage()) {
				//maintenant nous pouvons nous connecter à ADE en utilisant un login et un mot de passe
				this.login = login;
				this.password = password;	
				if(login_getProjects(login,password));
                     {
				    	}System.out.println(1);
			 }else  System.out.println("2");
		}else  System.out.println("3");
		}
	/*
	 * obtenir un certificat pour analyser un site web https
	 */
	public Boolean setCertificate() {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
		TrustManager[] trustAllCerts = new X509TrustManager[]{new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
		}
		};
		SSLContext sc;
		try {
			sc = SSLContext.getInstance("TSL");
		} catch (NoSuchAlgorithmException e) {
			try {
				sc = SSLContext.getInstance("SSL");
			} catch (NoSuchAlgorithmException e2) {
				JOptionPane.showMessageDialog(null, Constants.errMssg,Constants.errcertificate+e2.getMessage(), JOptionPane.ERROR_MESSAGE);				
			sc=null;
			return false;
		}}
		try {
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
		} catch (KeyManagementException e) {
			JOptionPane.showMessageDialog(null, Constants.errMssg,Constants.errcertificate+e.getMessage(), JOptionPane.ERROR_MESSAGE);				
			return false;
		}
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		return true;
	}

	/*
	 * etablire la première connexion sur ADE (chargement  formulaire de connexion)
	 * et récupérer les cookies
	 */
	 private Boolean loadFormPage(){
			HttpsURLConnection conn;
			Pattern cookiep = null ;
			String cookies = null ;
			try {
				conn = connectTo(Constants.indexPath);
				conn.connect();
				conn.getInputStream().close();
			    cookiep = Pattern.compile(Constants.COOKIESPATTERN);
			    cookies = conn.getHeaderField(Constants.SET_COOKIES);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, Constants.errConnection+e.getMessage(),Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
				return false;
			}
			if(cookies == null) {
				return true;
			}
			Matcher cookiematch =  cookiep.matcher(cookies);
			if(cookiematch.find())
				this.cookie=cookies.substring(cookiematch.start(1), cookiematch.end(1));
			return true; 
		}
	 /*
	  * se connecter en utilisant  le chemin donné et renvoyer la connexion
	  */
	 private HttpsURLConnection connectTo(String path) {
			URL addr;
			try {
				addr = new URL(Constants.url+path);
				HttpsURLConnection conn = (HttpsURLConnection)addr.openConnection();
				conn.setRequestMethod(Constants.getMethod);
				if(cookie != null)conn.addRequestProperty(Constants.PropertyCookie, login + Constants.DISPLAY_COOKIE_PART + Constants.JSESSIONID + cookie);
				return conn;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, Constants.errPath+e.getMessage(),Constants.errMssg, JOptionPane.ERROR_MESSAGE);				

				return null;
			}
		}
	 /*
	  * se connecter à ADE en utilisant le login et le mot de passe
	  * vérifier le login en récupérant la liste des projets
	  * params login and password
	  * ret  true si la connexion est réussie, false sinon 
	  * Si la connexion aboutit, retourner  la liste des projets
	  * 
	  */
		private Boolean login_getProjects(String login, String password) {
			String action=Constants.LogPage;
			String data=Constants.FormLoging+login+"&"+Constants.FormPass+password;
			URL addr;
			try {//Login
				addr = new URL(Constants.url+ action);
				HttpsURLConnection conn = (HttpsURLConnection)addr.openConnection();
				conn.setRequestMethod(Constants.PostMethod);
				conn.addRequestProperty(Constants.PropertyCookie, Constants.JSESSIONID +cookie);
				conn.addRequestProperty(Constants.PropertyContentType, Constants.valueContentType);
				conn.setDoOutput(true);
				conn.setDoInput(true);
				OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), Constants.cherSet);
				out.write(data);
				out.flush();
				out.close();
				conn.getInputStream().close();
			} catch (MalformedURLException e) {
				JOptionPane.showMessageDialog(null, Constants.errPath+e.getMessage(),Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
				return false;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, Constants.errPath+e.getMessage(),Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
				return false;
			}
			//vérifier la connexion et récupérer  les projets
			HttpsURLConnection conn;
			try {
				conn = connectTo(Constants.Projectspath);
				conn.connect();
				if(conn.getResponseCode() != HttpsURLConnection.HTTP_OK)
				{
					JOptionPane.showMessageDialog(null, Constants.errLogin0rPassword+Constants.Projectspath+" : "+conn.getResponseCode(),Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
                    return false;
				}
				Document ProjectsPage = Jsoup.parse(conn.getInputStream(), Constants.ParseFormat, Constants.url+Constants.Projectspath);
				//récupérer la liste des projets 
				this.projectList=Project.parsProject(ProjectsPage);
				conn.getInputStream().close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, Constants.errLogin+e.getMessage(),Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
                return false;
			}
		
			return true;
		}
	
	protected static final String attname = "name";

	protected static final String valueProjectId = "projectId";

	/*
	 * selectionner un projet 
	 * 
	 * récupérer les  Catégories 
	 */
	public Boolean selctProject(String id)   {
		this.project=getProjectFromList(id);
		String choises="projectId="+this.project.getId();
		if( 
		setChoice(Constants.setProjectPath,choises)
		&&
		display(Constants.pathSetPlannings)
		&&
		display(Constants.showPlannings)) {
			ParseurLevel=Level.CATEGORY;
			return getCategorys();
			
		}else return false;
	}
	/*
	 *	 * récupérer les  Catégories 
	 */
	private Boolean getCategorys() {
		HttpsURLConnection conn = connectTo(Constants.rootPath);
		try {
			conn.connect();
			Document CategoryhPage = Jsoup.parse(conn.getInputStream(), Constants.ParseFormat, Constants.url+Constants.rootPath);
			conn.getInputStream().close();
			project.setCategorys(Category.parsCategory(CategoryhPage));
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,Constants.errcategory+e.getMessage(),Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
			return false;
		}
	}
	private Boolean display(String url) {
		HttpsURLConnection conn;
		try {
			conn = connectTo(url);
			conn.connect();
			if(conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
				JOptionPane.showMessageDialog(null,conn.getResponseCode(),Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return true;	
	}
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	private Boolean setChoice(String path,String choises) {
		String action =path;
		URL addr;
		try {
			addr = new URL(Constants.url+ action);
			HttpsURLConnection conn = (HttpsURLConnection)addr.openConnection();
			conn.setRequestMethod(Constants.PostMethod);
			conn.addRequestProperty(Constants.PropertyCookie, Constants.JSESSIONID +cookie);
			conn.addRequestProperty(Constants.PropertyContentType, Constants.valueContentType);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), Constants.cherSet);
			out.write(choises);
			out.flush();
			conn.connect();
			out.close();
			conn.getInputStream().close();
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(null, Constants.errPath+e.getMessage(),Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
			return false;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, Constants.errChoise+e.getMessage(),Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
			return false;
		}

		return true;
	}
	private Project getProjectFromList(String id) {
		Project p =null;
		for (int i = 0; i < projectList.size(); i++) {
			if(projectList.get(i).getId().equals(id))
				return projectList.get(i);
		}
		return p;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public ArrayList<Project> getProjectList() {
		return projectList;
	}
	public void setProjectList(ArrayList<Project> projectList) {
		this.projectList = projectList;
	}
/*
 * selectionner une categorie et récupérer les branches 
 */
	public Boolean setCategory_getBranches(Category cat) {
		if(cat!=null)
			(cat).setIsSelected(true);
		else {
			JOptionPane.showMessageDialog(null,Constants.errNotExist+" "+cat,Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
			return false;
		}
		String choises="category="+cat.getId();
		if(setChoice(Constants.rootPath,choises)) {
			Document doc=getPage();
			project.getSelectedCategory().setBranch(Branch.parsBranch(doc));
			ParseurLevel=Level.BRANCH;
			return doc!=null;
		}else
		return false;
	}
	/*
	 * récupérer le code html de la page principale 
	 */
	private Document getPage() {
		HttpsURLConnection conn = connectTo(Constants.rootPath);
		try {
			conn.connect();
			Document page = Jsoup.parse(conn.getInputStream(), Constants.ParseFormat, Constants.url+Constants.rootPath);
			conn.getInputStream().close();
			return page;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,Constants.errBranche+e.getMessage(),Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
			return null;
		}
	
	}
	/*
	 * selectionner  une  branche et récupérer les sous branches  
	 */
	public boolean setBranch_getChild(Branch branch) {
		String open,select ;
		//DéSélectionner la branche déja sélectionné
					if(getProject().getSelectedCategory().getSelectedBranch()!=null) {
					//	getProject().getSelectedCategory().getSelectedBranch().setSelected(false);
						parcourBranches(branch,getProject().getSelectedCategory().getBranches());
					}
		if(branch!=null)
		branch.setSelected(true);
		else {
			JOptionPane.showMessageDialog(null,Constants.errBranchNotExist+" ",Constants.errMssg, JOptionPane.ERROR_MESSAGE);				
			return false;
		}
        if(branch.isLeaf()) {
			select="selectId="+branch.getId();	
		}else {
			//ouvrire la branche 
			select="selectBranchId="+branch.getId();
			open ="branchId="+branch.getId();
			if(!setChoice(Constants.rootPath,open))
				return false;
		}
		//selectionner  la  brancheou or la feuille
		if(setChoice(Constants.rootPath,select)) {
			Document doc=getPage();
			branch.setBranches(Branch.parsBranch(doc));
			return doc!=null;
		}else
		return false;
	}
	private void parcourBranches(Branch branch, ArrayList<Branch> list) {
		if(list.contains(branch)) {
			uncheckTheSelectedBranches(list);
			String select="selectBranchId="+branch.getId();
			setChoice(Constants.rootPath,select);
			}
		else {
			for (Branch b : list) {
				if(b.isIsselected())
					parcourBranches(branch,b.getBranches());
			}
		}
	}
	private void uncheckTheSelectedBranches(ArrayList<Branch> list) {
		for (Branch b : list) {
			if(b.isIsselected())
			{
				b.setSelected(false);
				uncheckTheSelectedBranches(b.getBranches());
			}
		}
	}
	public ArrayList<Creneaux> getTimeTable() throws Exception {
		HttpsURLConnection conn = connectTo(Constants.planningPath);
		conn.connect();
		Document timetable = Jsoup.parse(conn.getInputStream(), Constants.ISO, Constants.url+Constants.planningPath);

		conn.getInputStream().close();
		ArrayList<Creneaux> list =new ArrayList<>();
		Element t2 = timetable.body().child(0).child(0);
		for (int i = 2; i < t2.childNodeSize()-2; i++) {
			list.add(new Creneaux(t2.child(i)));
		}
		return list;
		//parsing...
		
		}

// Traitement des semaines 
	public void setWeek(int n,boolean reset) throws IOException{
		setChoice(Constants.BOUNDS,"week="+n+"&reset="+reset);
	}

	public ArrayList<AdeWeek> getWeeks() throws IOException{
		String path =Constants.PIANOWEEK;
		HttpsURLConnection conn = connectTo(path);
		
		conn.connect();
		if(conn.getResponseCode() != HttpsURLConnection.HTTP_OK)throw (new IOException(path+" : "+conn.getResponseCode()));
		Document pianoWeeks = Jsoup.parse(conn.getInputStream(), Constants.ISO,  Constants.url+path);
		conn.getInputStream().close();
		Element e = pianoWeeks.body().child(0);
		ArrayList<AdeWeek>  weeks=new ArrayList<AdeWeek>();
		for(Element w: e.children()){
			AdeWeek aw = new AdeWeek(w);
			weeks.add(aw);
		}

		return weeks;
	}
	public AdeWeek getSelectedWeek() {
		int i=0;
		ArrayList<AdeWeek> weeks = new ArrayList<AdeWeek>();
		try {
			weeks = getWeeks();
			while (!weeks.get(i).isSelected) {
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return weeks.get(i);
	}

	

	
}
