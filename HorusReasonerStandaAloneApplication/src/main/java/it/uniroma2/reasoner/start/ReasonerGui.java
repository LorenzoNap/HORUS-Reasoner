package it.uniroma2.reasoner.start;

import it.uniroma2.art.owlart.exceptions.ModelUpdateException;
import it.uniroma2.art.owlart.model.ARTStatement;
import it.uniroma2.art.owlart.model.ARTURIResource;
import it.uniroma2.art.owlart.models.RDFModel;
import it.uniroma2.reasoner.ConfigurationHandler.ConfigurationParameter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

public class ReasonerGui {
	
	private JFrame frame;
	
	private JPanel outputPanel;
	
	private JLabel status;
	
	private JPanel optionsOutputPanel;
	
	private File inferenceRuleFile = new File("temp_inference_rule_file");
	
	private File ontology;
	
	private File repoFolder;
	
	private RDFModel ont;
	
	private StartReasonerFacade startReasonerFacade;

	private JTextArea texAreaOutputRules;

	private JScrollPane scrollPaneOutput;

	private JSpinner spinnerUpDown;
	
	public ReasonerGui(StartReasonerFacade stratReasonerFacade) {
		this.startReasonerFacade = stratReasonerFacade;
		makeFrame();
		
	}


	private void loadDefaultInferenceRuleFile(JFrame frame) {
		InputStream stream = getClass().getClassLoader().getResourceAsStream("default_rule_file.txt");

		StringBuffer textInferenceRule= new StringBuffer();
		String sCurrentLine;

		BufferedReader br = new BufferedReader(new InputStreamReader(stream));

		OutputStream outputStream = null;
		//Read and save the inference rules file tex
		try {
			outputStream = new FileOutputStream(inferenceRuleFile);
			outputStream.write((new String()).getBytes());
			while ((sCurrentLine = br.readLine()) != null) {
				textInferenceRule.append(sCurrentLine);
				outputStream.write((sCurrentLine.toString()+"\n").getBytes());

			}

			outputStream.close();
			br.close();
			stream.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(frame, new JLabel(e.toString()));
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, new JLabel(e.toString()));
		}
		
	}


	private void makeFrame() {
		frame = new JFrame("Reasoner GUI");
		Container container = frame.getContentPane();
		
		
		container.setLayout(new BorderLayout());
		 status = new JLabel("");
		container.add(status,BorderLayout.NORTH);
		
		createMenu();
		
		JPanel configurations = new JPanel();
		configurations.setLayout(new FlowLayout());
		
		final JPanel jPanel = new JPanel();
		jPanel.setBorder(BorderFactory.createTitledBorder("ParametersConfigurations"));	
		
		
		configurations.add(jPanel);
		
		
		final JButton loadInferenceRuleButton = new JButton("Load Inference Rules");
		loadInferenceRuleButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
	            fc.showOpenDialog(frame);
	            if(fc.getSelectedFile() != null){
	            inferenceRuleFile = fc.getSelectedFile();
	        	JOptionPane.showMessageDialog(loadInferenceRuleButton, new JLabel("File loaded"));	
	        	status.setText("Inference Rules File loades");
	            }
			}
		});
	
		
		jPanel.add(loadInferenceRuleButton);
		
		final JButton loadDefaultInferenceRuleButton = new JButton("Load Default Inference Rules");
		loadDefaultInferenceRuleButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			

				loadDefaultInferenceRuleFile(frame);
	            JOptionPane.showMessageDialog(loadDefaultInferenceRuleButton, new JLabel("Rules loaded"));
	            status.setText("Inference Rules Default File loades");
			}
		});
		
		jPanel.add(loadDefaultInferenceRuleButton);
		
		final JButton showRulesButton = new JButton("Show Rules");
		showRulesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
//					JTextArea ta = new JTextArea(20, 60);
//					ta.read(new FileReader(inferenceRuleFile), null);
//					ta.setEditable(true);
					
					createFrameShowRule();
					
				//	JOptionPane.showMessageDialog(showRulesButton, new JScrollPane(ta));
					}
					catch (IOException ioe) {
						JOptionPane.showMessageDialog(frame, new JLabel(ioe.toString()));
					}
	            	
			}

			
		});
		
		jPanel.add(showRulesButton);
		
		final JCheckBox setOutput = new JCheckBox("Produce Output");
		
		
		
		jPanel.add(setOutput);
		
		JLabel setInferenceRule = new JLabel("How many time apply the reasoning process");
		
		
		jPanel.add(setInferenceRule);
		spinnerUpDown = new JSpinner();
		 
		
		jPanel.add(spinnerUpDown);
		
		JLabel rulesApply = new JLabel("Inference rules to apply");
		jPanel.add(rulesApply);
		
		
		final JTextArea jTextArea = new JTextArea();
		jTextArea.setMargin(new Insets(1, 1, 1, 1));

		
			
		
		jPanel.add(jTextArea);
		
		final JButton startReasoning = new JButton("Start Reasoning");
		startReasoning.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				boolean produceOutput = setOutput.isSelected();
				String rulesID = jTextArea.getText();
				int count = (Integer)spinnerUpDown.getValue();
				
				
				
				if(ontology != null && repoFolder != null){
					LoadModel loadModel = new LoadModel();
					try {
						startReasonerFacade.setParameter(ConfigurationParameter.PRODUCE_OUTPUT, String.valueOf(produceOutput));
						startReasonerFacade.setParameter(ConfigurationParameter.WHICHRULEEXECUTE, rulesID);
						startReasonerFacade.setParameter(ConfigurationParameter.NUMBER_OF_EXECUTION, String.valueOf(count));
						ont = loadModel.loadRDFModel(repoFolder.getAbsolutePath(), ontology.getAbsolutePath());
						status.setText("Start Reasoning Operation....");
						List<ARTStatement> results=  startReasonerFacade.startReasoner(ont, inferenceRuleFile);
						if (results.size() > 0){
							if(startReasonerFacade.getInputOutputHanlder().getNumberOfIteration() < count || count == 0 ){
								JOptionPane.showMessageDialog(loadDefaultInferenceRuleButton, new JLabel("Have not been found new inffered triples after the execution of "+startReasonerFacade.getInputOutputHanlder().getNumberOfIteration()+
										" reasoning iteration"));
							}
							if(produceOutput){
							Component[] com = outputPanel.getComponents();
							//Inside you action event where you want to disable everything
							//Do the following
							for (int a = 0; a < com.length; a++) {
							     com[a].setEnabled(true);
							}
							texAreaOutputRules.setText(startReasonerFacade.getOutputList().printOuput());
							 texAreaOutputRules.setEnabled(true);
							 scrollPaneOutput.setEnabled(true);
							createPanelOutputOptions();
							}
							else{
								JOptionPane.showMessageDialog(loadDefaultInferenceRuleButton, new JLabel("Triple founded: "+results.size()));
							}
						}
						else{
							JOptionPane.showMessageDialog(loadDefaultInferenceRuleButton, new JLabel("No new triples found"));
						}
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(frame, e1.toString(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				if (ontology == null){
					  JOptionPane.showMessageDialog(loadDefaultInferenceRuleButton, new JLabel("You must load an Ontology file"));
				}
				else if(repoFolder == null){
					JOptionPane.showMessageDialog(loadDefaultInferenceRuleButton, new JLabel("You must load a repository Folder"));
				}
				
			}
		});
	
		jPanel.add(startReasoning);
		
		GridLayout gridLayout = new GridLayout(0,1);
		gridLayout.setVgap(5);
		jPanel.setLayout(gridLayout);
		
		
		
		container.add(configurations,BorderLayout.WEST);
		
		container.add(createOutputPanel(),BorderLayout.SOUTH);
		
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.pack();
		frame.setVisible(true);
		
		try {
			setOutput.setSelected(Boolean.parseBoolean(startReasonerFacade.getConfigurationParameter(ConfigurationParameter.PRODUCE_OUTPUT)));
			int count =Integer.parseInt(startReasonerFacade.getConfigurationParameter(ConfigurationParameter.NUMBER_OF_EXECUTION));
			 jTextArea.setText(startReasonerFacade.getConfigurationParameter(ConfigurationParameter.WHICHRULEEXECUTE));
			 spinnerUpDown.setModel(new SpinnerNumberModel(count, 0, 20, 1));
	         spinnerUpDown.setEditor(new JSpinner.NumberEditor(spinnerUpDown, "0"));
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(frame, e1.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(frame, e1.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		 
        
		loadDefaultInferenceRuleFile(frame);
	}
	
	public void createMenu(){
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		
		JMenu menu = new JMenu("File");
		menuBar.add(menu);
		
		JMenu menuOntology = new JMenu("Ontology");
		menuBar.add(menuOntology);
		
		JMenuItem ontologyItem = new JMenuItem("Load Ontology");
		ontologyItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
	            fc.showOpenDialog(frame);
	            if(fc.getSelectedFile() != null){
	            ontology = fc.getSelectedFile();
	            status.setText("Ontology Loaded");
	            }
			}
		});
		menuOntology.add(ontologyItem);
		
		
		JMenuItem loadRepo = new JMenuItem("Load Repository");
		loadRepo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            fc.showOpenDialog(frame);
	            if(fc.getSelectedFile() != null){
	            repoFolder = fc.getSelectedFile();
	            status.setText("Repository Loaded");
	            }
				
			}
		});
		menuOntology.add(loadRepo);
		
		
		JMenuItem item = new JMenuItem("Quit");
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			
				if(ont != null){
					try {
						ont.close();
					} catch (ModelUpdateException e1) {
						
						JOptionPane.showMessageDialog(frame, "Unable to close RDF Store","Error",JOptionPane.ERROR_MESSAGE);
					}
				}
				((JFrame) frame).dispose();
			}
		});
		menu.add(item);
	}
	
	private void createFrameShowRule() throws FileNotFoundException, IOException {
		final JFrame showRuleFrame = new JFrame("Show Inference Rules");
		Container container = showRuleFrame.getContentPane();
		container.setLayout(new BorderLayout());
		
		
		
		final JTextArea ta = new JTextArea(20, 60);
		ta.read(new FileReader(inferenceRuleFile), null);
		ta.setEditable(true);
		JScrollPane jScrollPane = new JScrollPane(ta);
		
		container.add(jScrollPane, BorderLayout.CENTER);
		
		 JPanel buttons = new JPanel();	
		 outputPanel.setLayout(new FlowLayout());
		
		 JButton upload = new JButton("Save changes");
		 upload.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				OutputStream outputStream = null;
				
					try {
						outputStream = new FileOutputStream(inferenceRuleFile);
						//Flush the current file
						outputStream.write((new String()).getBytes());
						//Write into current file the user file
						outputStream.write((ta.getText()).getBytes());
						outputStream.close();	
						((JFrame) showRuleFrame).dispose();
						JOptionPane.showMessageDialog(frame, new JLabel("File saved"));
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(frame, new JLabel(e1.toString()));
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(frame, new JLabel(e1.toString()));
					}
						
			}
			
		});
		 buttons.add(upload);
		 JButton cancel = new JButton("Cancel");
		 cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				((JFrame) showRuleFrame).dispose();
				
			}
		});		
		 buttons.add(cancel);
		 
		container.add(buttons,BorderLayout.SOUTH);
		showRuleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		showRuleFrame.pack();
		showRuleFrame.setVisible(true);
		
	}
	
	
	public JPanel createOutputPanel(){
		 outputPanel = new JPanel();
		 outputPanel.setBorder(BorderFactory.createTitledBorder("Show text Output"));	
		 outputPanel.setLayout(new FlowLayout());
		
		JButton showText = new JButton("Show Output Text");
		
	    texAreaOutputRules = new JTextArea(40, 60);
	    texAreaOutputRules.setEnabled(false);
		scrollPaneOutput =  new JScrollPane(texAreaOutputRules);
		scrollPaneOutput.setEnabled(false);
		frame.getContentPane().add(scrollPaneOutput,BorderLayout.CENTER);
		
		
		
		showText.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 texAreaOutputRules.setEnabled(true);
				 scrollPaneOutput.setEnabled(true);
				
				texAreaOutputRules.setText(startReasonerFacade.getOutputList().printOuput());
				
			}
		});
		
		outputPanel.add(showText);
		
		JButton showGraph = new JButton("Show graph");
		showGraph.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				startReasonerFacade.showGraphOnWindow();
				
			}
		});
		outputPanel.add(showGraph);
		
		
		Component[] com = outputPanel.getComponents();
		//Inside you action event where you want to disable everything
		//Do the following
		for (int a = 0; a < com.length; a++) {
		     com[a].setEnabled(false);
		}
		return outputPanel;
	}
	
	private void createPanelOutputOptions(){
		if(optionsOutputPanel != null){
			optionsOutputPanel.removeAll();
		}
		JPanel jPanel = new JPanel();
		jPanel.setBorder(BorderFactory.createTitledBorder("Output options"));	
		GridLayout gridLayout = new GridLayout(0,1);
		gridLayout.setVgap(5);
		jPanel.setLayout(gridLayout);
		

		optionsOutputPanel = new JPanel();
		optionsOutputPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		optionsOutputPanel.add(jPanel);
		
		final JPanel searchTriple = new JPanel();
		searchTriple.setBorder(BorderFactory.createTitledBorder("Filter Triple"));
		GridLayout gridLayout2 = new GridLayout(0,1);
		gridLayout2.setVgap(5);
		searchTriple.setLayout(gridLayout2);
		jPanel.add(searchTriple);
		
		String inferenceNames = startReasonerFacade.getInferenceRulesName();
		String[] infStrings = inferenceNames.split(",");
		for( int i = 0; i < infStrings.length; i++){
			JCheckBox box = new JCheckBox(infStrings[i]);
			searchTriple.add(box);
		}
	
		
		JButton applyFilter = new JButton("Apply Filter");
		applyFilter.setLayout(new FlowLayout());
		applyFilter.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder filters = new StringBuilder();
				
				Component[] com = searchTriple.getComponents();
				for (int a = 0; a < com.length; a++) {
				    if (com[a] instanceof JCheckBox && ((JCheckBox)com[a]).isSelected()){
				    	filters.append(((JCheckBox)com[a]).getText()+",");
				    }
				}
				texAreaOutputRules.setText(startReasonerFacade.filterOutput(filters.toString()));
				
			}
		});
		searchTriple.add(applyFilter);
		//applyFilterButtonPanel.add(applyFilter);
		//jPanel.add(applyFilterButtonPanel);
		
		
		JPanel searchBoxPanel = new JPanel();
		searchBoxPanel.setBorder(BorderFactory.createTitledBorder("Search Triple"));	
		GridLayout gridLayout3 = new GridLayout(0,1);
		gridLayout3.setVgap(5);
		searchBoxPanel.setLayout(gridLayout3);
		jPanel.add(searchBoxPanel);
		
		JLabel labelSearch = new JLabel("Subject");
		searchBoxPanel.add(labelSearch);
		final JTextArea searchAreaSubject = new JTextArea();
		searchBoxPanel.add(searchAreaSubject);
		
		JLabel labelPredicate = new JLabel("Predictae");
		searchBoxPanel.add(labelPredicate);
		final JTextArea searchAreaPredicate = new JTextArea();
		searchBoxPanel.add(searchAreaPredicate);
		
		JLabel labelObject = new JLabel("Object");
		searchBoxPanel.add(labelObject);
		final JTextArea searchAreaObject = new JTextArea();
		searchBoxPanel.add(searchAreaObject);
		
		final JButton buttonSearch = new JButton("Search");
		buttonSearch.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(searchAreaSubject.getText().equals("") || searchAreaSubject.getText().length() < 5 || 
						searchAreaPredicate.getText().equals("") || searchAreaPredicate.getText().length() < 5 || 
						searchAreaObject.getText().equals("") || searchAreaObject.getText().length() < 5){
					JOptionPane.showMessageDialog(buttonSearch, "You insert wrong input", "Error", JOptionPane.ERROR_MESSAGE);
				}
				else{
					//Convert string to ART resource
					try{
					ARTURIResource subj = ont.createURIResource(searchAreaSubject.getText().replace("(", "").replace(")", ""));
					ARTURIResource predicate =  ont.createURIResource(searchAreaPredicate.getText().replace("(", "").replace(")", ""));
					ARTURIResource object = ont.createURIResource(searchAreaObject.getText().replace("(", "").replace(")", ""));
					//Create new statement with resources
					ARTStatement stm = ont.createStatement(subj, predicate, object);
					String result = startReasonerFacade.searchHistoryTriple(stm);
					if(result.equals("")){
						JOptionPane.showMessageDialog(buttonSearch, "No triple found", "Info", JOptionPane.INFORMATION_MESSAGE);

					}
					else{
						texAreaOutputRules.setText(result);
						}
					}
					catch(IllegalArgumentException exception){
						JOptionPane.showMessageDialog(buttonSearch, exception.toString(), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				
				
			}
		});
		searchBoxPanel.add(buttonSearch);
		
		
		frame.getContentPane().add(optionsOutputPanel,BorderLayout.EAST);
	}
}
