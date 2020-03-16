package gsonConvert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

public class ControllerFXMLPrincipal{

	private JsonArray jsonArray;
	
    @FXML
    private Button btnArq;

    @FXML
    private ProgressBar ProgBarArq;

    @FXML
    private TextArea txtArq;
    
    @FXML
    private Button btnSalvar;

    @FXML
    public void abrirArq(ActionEvent event) {
    	JFileChooser escolher = new JFileChooser();
    	escolher.setCurrentDirectory(new File(System.getProperty("user.home")));
    	int result = escolher.showOpenDialog(null);
    	ProgBarArq.setProgress(0.0);
    	
    	if(result == JFileChooser.APPROVE_OPTION) {
    		new Thread(new Runnable() {
				
				@Override
				public void run() {
						try {
							convertArq(escolher.getSelectedFile());
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
			}).start();
    	}
    }

	public void escreverArq(JsonArray jsonArray) throws InterruptedException {
    	txtArq.clear();
    	Gson gson = new GsonBuilder().setPrettyPrinting().create();
    	String arrayListToJson = gson.toJson(jsonArray);
    	txtArq.appendText(arrayListToJson);
    }
	
	@FXML
    void salvarArq(ActionEvent event) {
    		JFileChooser salvar = new JFileChooser();
        	int result = salvar.showSaveDialog(null);
        	if(result == JFileChooser.APPROVE_OPTION) {
        		Path path = Paths.get(salvar.getSelectedFile().getPath() + ".json");
        		try {
					Path novoDir = Files.createFile(path);
					criarJson(novoDir);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
    }
	
    public void criarJson(Path path) {
    	
		try {
			BufferedWriter escrever;
			escrever = Files.newBufferedWriter(path, StandardCharsets.UTF_16, StandardOpenOption.APPEND);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			escrever.write(gson.toJson(jsonArray));
			escrever.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	public void convertArq(File arq) throws InterruptedException {
    	jsonArray = new JsonArray();
		try {
			String line;
			
			float qtd = qtdOperacoes(arq);
			int count = 1;
			
			Path path = Paths.get(arq.getPath());
			boolean flag = true;
			List<String> colunas = null;
			BufferedReader ler = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			while((line = ler.readLine()) != null) {
				if(flag) {
					flag = false;
					colunas = Arrays.asList(line.split(","));
				}
				else {
					JsonObject obj = new JsonObject();
					List<String> valores = Arrays.asList(line.split(","));
					for(int i = 0; i < colunas.size(); i++) {
						
						obj.addProperty(colunas.get(i), valores.get(i));
						
					}
					jsonArray.add(obj);
					ProgBarArq.setProgress(count / qtd);
					count++;
				}
			}
			//escreverArq(jsonArray);
		}
		catch (IOException e) {
			// TODO: handle exception
		}
    }
	
	public float qtdOperacoes(File arq) {
		try {
			float qtd = -1;
			String line;
			Path path = Paths.get(arq.getPath());
			BufferedReader ler = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			while((line = ler.readLine()) != null) {
				qtd++;
			}
			return qtd;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}
}