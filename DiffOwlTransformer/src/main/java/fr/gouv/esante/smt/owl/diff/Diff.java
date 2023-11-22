package fr.gouv.esante.smt.owl.diff;

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;


import fr.gouv.esante.smt.owl.core.OWLChangeType;
import fr.gouv.esante.smt.owl.core.SyntacticAxiomChange;
import fr.gouv.esante.smt.owl.core.SyntacticDiff;
import fr.gouv.esante.smt.owl.core.SyntacticDiffOutput;
import fr.gouv.esante.smt.owl.core.OntologyHandler;
//import cz.cvut.kbss.owldiff.syntax.ManchesterSyntax;


import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;
import java.net.URI;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.io.OWLRendererException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OntologyConfigurator;
import org.slf4j.LoggerFactory;


@Slf4j
@Command(name = "diff")
public class Diff {
	
	private static Logger LOG = Logger.getLogger(Diff.class.getName());
	


    @Arguments(title = "File paths", description = "Path of the oldFile and newFile")
    private List<String> paths;

    public void run() throws OWLOntologyCreationException, OWLRendererException, IOException {
        
    	LOG.setLevel(Level.ALL);
    	LOG.addHandler(new java.util.logging.ConsoleHandler());
    	LOG.setUseParentHandlers(false);
    	
	     

    	
    	if (paths.size() != 3) {
        	LOG.severe("Exactly three files must be provided");

           return;
        }

        String oldFF = paths.get(0);
        
        String newFF = paths.get(1);
        
        String outputExcel = paths.get(2);
        		
        

        URI oldF;
        URI newF;
        if (new File(oldFF).exists()) {
            oldF = new File(oldFF).getAbsoluteFile().toURI();
        } else {
        	LOG.severe("File N-1 does not exist ");
            throw new RuntimeException();
        }

        if (new File(newFF).exists()) {
            newF = new File(newFF).getAbsoluteFile().toURI();
        } else {
        	LOG.severe("File N does not exist ");
            throw new RuntimeException();
        }
        
        
        
        
        if (oldF.isAbsolute() && newF.isAbsolute()) {
        	
            final OWLOntologyManager originalM = OWLManager
                .createOWLOntologyManager();
            originalM.setOntologyConfigurator(
                new OntologyConfigurator()
                    .setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT)
                    .setReportStackTraces(true)
            .setStrict(false));
           
           originalM.getIRIMappers().add( (ontologyIRI) -> {
           if (Pattern.compile(oldFF).matcher(ontologyIRI.toString()).matches()) {
               
            	return ontologyIRI;
            } else {
            	
                return IRI.create(String.format("file://%s", ontologyIRI.toString()));
            }});
          
            final OWLOntology originalO =
                originalM.loadOntologyFromOntologyDocument(new IRIDocumentSource(
                    IRI.create(oldF)));
            
         

            final OWLOntologyManager updateM = OWLManager.createOWLOntologyManager();
            updateM.setOntologyConfigurator(new OntologyConfigurator().setMissingImportHandlingStrategy(
                MissingImportHandlingStrategy.SILENT)
                .setReportStackTraces(true)
                .setStrict(false)
            );
            
            updateM.getIRIMappers().add( (ontologyIRI) -> {
               if (Pattern.compile(newFF).matcher(ontologyIRI.toString()).matches()) {
                	 
                    return ontologyIRI;
                } else {
                	 
                    return IRI.create(String.format("file://%s", ontologyIRI.toString()));
                }});
            
            OWLOntology updateO =
                updateM.loadOntologyFromOntologyDocument(new IRIDocumentSource(IRI.create(newF)));
           
            
            
            
            
            
            SyntacticDiff d = new SyntacticDiff(new OntologyHandler() {
                @Override public OWLOntology getOriginalOntology() {
                    return originalO;
                }

                @Override public OWLOntology getUpdateOntology() {
                    return updateO;
                }
            });

            final SyntacticDiffOutput o = d.diff();
            
           
           
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("FirstSheet"); 
            
            //sheet2
           // HSSFSheet sheet2 = workbook.createSheet("SecondSheet");
            
          
            
            HSSFRow rowhead = sheet.createRow((short)0);
            
            //rowhead2
            //HSSFRow rowhead2 = sheet2.createRow((short)0);
            
          
            
            HSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellStyle.setWrapText(true);
            cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            
            
            HSSFCell cellA1 = rowhead.createCell(0);
            cellA1.setCellValue("Concept_IRI");
            cellA1.setCellStyle(cellStyle);
            
            
            HSSFCell cellA2 = rowhead.createCell(1);
            cellA2.setCellValue("Type");
            cellA2.setCellStyle(cellStyle);
            
            HSSFCell cellA3 = rowhead.createCell(2);
            cellA3.setCellValue("Relation");
            cellA3.setCellStyle(cellStyle);
            
            HSSFCell cellA4 = rowhead.createCell(3);
            cellA4.setCellValue("Ancienne Valeur");
            cellA4.setCellStyle(cellStyle);
            
            HSSFCell cellA5 = rowhead.createCell(4);
            cellA5.setCellValue("Nouvelle Valeur");
            cellA5.setCellStyle(cellStyle);
            
            
          //nouveau Sheett
			/*
			 * HSSFCell cellA11 = rowhead2.createCell(0);
			 * cellA11.setCellValue("Concept_IRI"); cellA11.setCellStyle(cellStyle);
			 * 
			 * 
			 * 
			 * HSSFCell cellA21 = rowhead2.createCell(1); cellA21.setCellValue("Type");
			 * cellA21.setCellStyle(cellStyle);
			 * 
			 * HSSFCell cellA31 = rowhead2.createCell(2); cellA31.setCellValue("Relation");
			 * cellA31.setCellStyle(cellStyle);
			 * 
			 * HSSFCell cellA41 = rowhead2.createCell(3);
			 * cellA41.setCellValue("Ancienne Valeur"); cellA41.setCellStyle(cellStyle);
			 * 
			 * HSSFCell cellA51 = rowhead2.createCell(4);
			 * cellA51.setCellValue("Nouvelle Valeur"); cellA51.setCellStyle(cellStyle);
			 */
            
            
           
          int num =1; 
          //short num_sheet =1;
          
          for(String id: o.getListaxiomsChanges().keySet()) {
            	
        	  //System.out.println(id.split("\\|")[0] + " "+o.getListaxiomsChanges().get(id).getType()+ " " + id.split("\\|")[1] + " "
                //  	+ o.getListaxiomsChanges().get(id).getOld_Value()+ " "+ o.getListaxiomsChanges().get(id).getNew_Value());
        	//  if(num <32767) {
        	  System.out.println(" num "+num);
        	  HSSFRow row = sheet.createRow(num);
              row.createCell(0).setCellValue(id.split("\\|")[0]);
              row.createCell(1).setCellValue(o.getListaxiomsChanges().get(id).getType());
              row.createCell(2).setCellValue(id.split("\\|")[1]);
              row.createCell(3).setCellValue(o.getListaxiomsChanges().get(id).getOld_Value());
              row.createCell(4).setCellValue(o.getListaxiomsChanges().get(id).getNew_Value());
            	
        	  num++;
        	  
				/*
				 * }else {
				 * 
				 * System.out.println(" num_sheet "+num_sheet); HSSFRow row =
				 * sheet2.createRow((short)num_sheet);
				 * row.createCell(0).setCellValue(id.split("\\|")[0]);
				 * row.createCell(1).setCellValue(o.getListaxiomsChanges().get(id).getType());
				 * row.createCell(2).setCellValue(id.split("\\|")[1]);
				 * row.createCell(3).setCellValue(o.getListaxiomsChanges().get(id).getOld_Value(
				 * ));
				 * row.createCell(4).setCellValue(o.getListaxiomsChanges().get(id).getNew_Value(
				 * ));
				 * 
				 * num_sheet++;
				 * 
				 * }
				 */
        	
            	
            }
            
         sheet.autoSizeColumn(0);
         sheet.autoSizeColumn(1);
         sheet.autoSizeColumn(2);
         sheet.autoSizeColumn(3);
         sheet.autoSizeColumn(4);
         
			/*
			 * sheet2.autoSizeColumn(0); sheet2.autoSizeColumn(1); sheet2.autoSizeColumn(2);
			 * sheet2.autoSizeColumn(3); sheet2.autoSizeColumn(4);
			 */
          FileOutputStream fileOut = new FileOutputStream(outputExcel);
          workbook.write(fileOut);
          fileOut.close();
         // workbook.close();
          //System.out.println("Your excel file has been generated!");
      	  LOG.info("Your excel file has been generated!");



        } else {
           // log.error("Files are not absolute");
        }
    }

   
    
    
}
