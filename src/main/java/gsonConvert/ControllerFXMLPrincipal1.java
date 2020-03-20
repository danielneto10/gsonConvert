package gsonConvert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;

public class ControllerFXMLPrincipal1 implements Tempos {
	
    @FXML
    private Button btnArq;

    @FXML
    private ProgressBar ProgBarArq;

    @FXML
    private ListView<String> listView;
    
    @FXML
    private Button btnSalvar;

    private JsonArray jsonArray;
    
    Instant inicio;
    Instant fim;
    
    @FXML
    public void abrirArq(ActionEvent event) {
    	JFileChooser escolher = new JFileChooser();
    	escolher.setCurrentDirectory(new File(System.getProperty("user.home")));
    	int result = escolher.showOpenDialog(null); // Abrir Janela para selecionar o arquivo
    	ProgBarArq.setProgress(0.0); // Setar o valor da barra de progresso pra 0
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
	
	@FXML
    void salvarArq(ActionEvent event) {
    		JFileChooser salvar = new JFileChooser();
        	int result = salvar.showSaveDialog(null); // Abrir tela pra salvar o arquivo
        	if(result == JFileChooser.APPROVE_OPTION) {
        		Path path = Paths.get(salvar.getSelectedFile().getPath() + ".json"); // Defenir o arquvio com a extensão .json
        		try {
					Path novoDir = Files.createFile(path); // Criação do arquivo
					criarJson(novoDir); // Escrever o texto no formato json dentro do arquivo
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error Dialog");
					alert.setHeaderText("Erro na criação do arquivo");
					alert.setContentText("Já existe um arquivo com o mesmo nome \nUse um nome diferente.");
					alert.showAndWait();
				}
        }
    }
	
    public void criarJson(Path path) {
		Instant iniMed;
		Instant fimMed;
		long duracaoMed;
		
		try {
			iniMed = Instant.now();
			BufferedWriter escrever;
			escrever = Files.newBufferedWriter(path, StandardCharsets.UTF_16, StandardOpenOption.APPEND);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			escrever.write(gson.toJson(jsonArray)); // Escrever o texto no arquivo criado
			escrever.close();
			fimMed = Instant.now();
			
			System.out.println("Gravar Arquivo");
			duracaoMed = Duration.between(iniMed, fimMed).toMillis();
			System.out.println("Duracao media: " + duracaoMed / jsonArray.size() + "ms\n");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	public void convertArq(File arq) throws InterruptedException {
    	jsonArray = new JsonArray();
		try {
			long maior = Long.MIN_VALUE;
			long menor = Long.MAX_VALUE;
			long duracao;
			
			Instant iniMed;
			Instant fimMed;
			long duracaoMed;
			
			
			String line;
			float qtd = qtdOperacoes(arq); // Defenir o numero de linhas do arquivo
			int count = 1;
			
			Path path = Paths.get(arq.getPath());
			boolean flag = true; 
			List<String> colunas = null;
			BufferedReader ler = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			iniMed = Instant.now();
			while((line = ler.readLine()) != null) {
				if(flag) { // Flag pra pegar apenas a primeira linha
					flag = false;
					colunas = Arrays.asList(line.split(",")); // Criar uma lista de array das colunas separados por ","
				}
				else {
					inicio = Instant.now();
					JsonObject obj = new JsonObject();
					List<String> valores = Arrays.asList(line.split(",")); // Criar um lista de array dos valores separados por ","
					for(int i = 0; i < colunas.size(); i++) {
						
						obj.addProperty(colunas.get(i), valores.get(i)); // Representar cada coluna com seu valor
						
					}
					fim = Instant.now();
					duracao = Duration.between(inicio, fim).toMillis();
					maior = max(duracao, maior);
					menor = min(duracao, menor);
					
					jsonArray.add(obj); // Adicionar o JsonObject pra um JsonArray
					listView.getItems().add(obj.toString()); // Escrever todos os objetos do json em uma listview
					ProgBarArq.setProgress(count / qtd); // Progresso da barra de acordo com o numero de objetos feitos
					count++;
				}
			}
			fimMed = Instant.now();
			System.out.println("Converter Arquivo");
			System.out.println("Duracao maxima: " + maior + "ms");
			System.out.println("Duracao minima: " + menor + "ms");
			
			duracaoMed = Duration.between(iniMed, fimMed).toMillis();
			System.out.println("Duracao media: " + duracaoMed / qtd + "ms\n");
			
		}
		catch (IOException e) {
			// TODO: handle exception
		}
    }
	
	public float qtdOperacoes(File arq) {
		try {
			long maior = Long.MIN_VALUE;
			long menor = Long.MAX_VALUE;
			long duracao;
			
			Instant iniMed;
			Instant fimMed;
			long duracaoMed;
			
			float qtd = -1;
			String line;
			Path path = Paths.get(arq.getPath());
			BufferedReader ler = Files.newBufferedReader(path, StandardCharsets.UTF_8);
			iniMed = Instant.now();
			while((line = ler.readLine()) != null) {
				inicio = Instant.now();
				qtd++;
				fim = Instant.now();
				duracao = Duration.between(inicio, fim).toMillis();
				
				maior = max(duracao, maior);
				menor = min(duracao, menor);
			}
			fimMed = Instant.now();
			System.out.println("Ler arquivos");
			System.out.println("Duracao maxima: " + maior + "ms");
			System.out.println("Duracao minima: " + menor + "ms");
			
			duracaoMed = Duration.between(iniMed, fimMed).toMillis();
			System.out.println("Duracao media: " + duracaoMed / qtd + "ms\n");
			
			return qtd;
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
	}

	@Override
	public long max(long v, long max) {
		if(v > max) {
			return v;
		}
		else {
			return max;
		}
	}

	@Override
	public long min(long v, long min) {
		if(v < min) {
			return v;
		}
		else {
			return min;
		}
	}

	@Override
	public long med(long v) {
		// TODO Auto-generated method stub
		return 0;
	}
}