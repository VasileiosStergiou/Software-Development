package backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import dom2app.SimpleTableModel;

public class MainController implements IMainController {
	private String[] columnNames;
	private ArrayList<Context> taskList;
	
	public MainController() {
		columnNames = new String[6];
		columnNames[0] = "TaskID";
		columnNames[1] = "TaskText";
		columnNames[2] = "MamaID";
		columnNames[3] = "Start";
		columnNames[4] = "End";
		columnNames[5] = "Cost";
	}

	@Override
	public SimpleTableModel load(String fileName, String delimiter) {
	    
	    SimpleTableModel tableModel = null;
	    ArrayList<Integer> complexTaskList = new ArrayList<Integer>();
	    ArrayList<Integer> topLevelTask = new ArrayList<Integer>();
	    ArrayList<Context> unsortedTaskList = new ArrayList<Context>();
	    ArrayList<String[]> result = new ArrayList<String[]>();
	    taskList = new ArrayList<Context>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line;
			
			while((line = reader.readLine()) != null) {
				String[] parsedFields = line.split(delimiter);
				int taskID = Integer.parseInt(parsedFields[0]);
				String text = parsedFields[1];
				int mamaID = Integer.parseInt(parsedFields[2]);
				Context context = new Context(taskID, mamaID, text);
				unsortedTaskList.add(context);
				
				if (parsedFields.length == 3) {
					complexTaskList.add(unsortedTaskList.size()-1);
				} else {
					int start = Integer.parseInt(parsedFields[3]);
					int end = Integer.parseInt(parsedFields[4]);
					int cost = Integer.parseInt(parsedFields[5]);
					
					context.setStart(start);
					context.setEnd(end);
					context.setCost(cost);
				}
				
				if (mamaID == 0) {
					topLevelTask.add(unsortedTaskList.size()-1);
				}
			}
			
			for (Integer index: complexTaskList) {
				int id = unsortedTaskList.get(index).getID();
				Context mamaTask = unsortedTaskList.get(index);
				int minStart = Integer.MAX_VALUE;
				int maxEnd = 0;
				float totalCost = 0;
				
				for (Context context: unsortedTaskList) {	
					if (id == context.getMamaID()) {
						
						if (context.getStart() < minStart) {
							minStart = context.getStart();
						}
						
						if (context.getEnd() > maxEnd) {
							maxEnd = context.getEnd();
						}
						
						totalCost += context.getCost();
					}
				}
				
				mamaTask.setCost(totalCost);
				mamaTask.setStart(minStart);
				mamaTask.setEnd(maxEnd);	
			}
			
			for (Integer index: topLevelTask) {
				Context context = unsortedTaskList.get(index);				
				boolean elementInserted = false;
				
				for (int i = 0; i < taskList.size(); i++) {
					Context sortedListContext = taskList.get(i);
					if ((context.getStart() < sortedListContext.getStart()) || 
					   (context.getStart() == sortedListContext.getStart() && 
					    context.getID() < sortedListContext.getID())) {
						taskList.add(i, context);
						elementInserted = true;
						break;
					}
				}

				if (!elementInserted) {
					taskList.add(context);
				}
			}
			
			
			for (Context context: unsortedTaskList) {	
				if (context.getMamaID() == 0) {
					continue;
				}
				
				int mamaIndex = 0;
				
				for (int i = 0; i < taskList.size(); i++) {
					if (context.getMamaID() == taskList.get(i).getID()) {
						mamaIndex = i;
						break;
					}
				}
				
				boolean elementInserted = false;
				
				for (int i = mamaIndex + 1; i < taskList.size(); i++) {
					int currentMamaID = taskList.get(i).getMamaID();
					
					if (currentMamaID == 0) {
						taskList.add(i, context);
						elementInserted = true;
						break;
					}
					
					Context sortedListContext = taskList.get(i);
					
					if (context.getStart() < sortedListContext.getStart() || 
					   (context.getStart() == sortedListContext.getStart() && context.getID() < sortedListContext.getID())) {

						taskList.add(i,context);
						elementInserted = true;
						break;
					}
				}
				
				if (!elementInserted) {
					taskList.add(context);
				}
			}
			
			
			for (Context context: taskList) {
				result.add(context.asArray());
			}
			
			tableModel = new SimpleTableModel("dias", "ares", columnNames, result);
			reader.close();

		} catch(IOException e) {
			e.printStackTrace();
		}

		return tableModel;
	}

	@Override
	public SimpleTableModel getTasksByPrefix(String prefix) {
		
		ArrayList<String[]> result = new ArrayList<String[]>();

		for (Context context: taskList) {
			String[] contextInfo = context.asArray();
			
			if (contextInfo[1].contains(prefix)) {
				result.add(contextInfo);
			}
		}

		return new SimpleTableModel("dias", "ares", columnNames, result);
	}

	@Override
	public SimpleTableModel getTaskById(int id) {
		
		String idComposite = Integer.toString(id);
		ArrayList<String[]> result = new ArrayList<String[]>();

		for (Context context: taskList) {
			
			String[] contextInfo = context.asArray();
			
			if (contextInfo[0].contains(idComposite)) {
				result.add(contextInfo);
			}
		}

		return new SimpleTableModel("dias", "ares", columnNames, result);
	}

	@Override
	public SimpleTableModel getTopLevelTasks() {
		
		ArrayList<String[]> result = new ArrayList<String[]>();

		for (Context context: taskList) {

			if (context.getMamaID() == 0) {
				result.add(context.asArray());
			}
		}

		return new SimpleTableModel("dias", "ares", columnNames, result);
	}

	@Override
	public int createReport(String path, ReportType type) {

		String result = "";

		if (type == ReportType.HTML) {
			result += "<!doctype html>\n<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"text/html;charset=windows-1253\">\n<title>Gantt Project Data</title>\n</head>\n<body>\n\n<table>\n<tr>\n";
			
			for (String field: columnNames) {
				result += "<td>" + field + "</td>	";
			}
			
			result += "</tr>\n\n";
			
			for (Context context:taskList) {
				result += "<tr>\n";
				
				for (String field: context.asArray()) {
					result += "<td>" + field + "</td>	";
				}
				
				result += "</tr>\n\n";
				
			}

			result += "</table></body>\n</html>";
			
		} else if (type == ReportType.TEXT) {
			result += String.join("	", columnNames) + "\n";
			
			for (Context context:taskList) {
				result += String.join("	", context.asArray()) + "\n";
			}

		} else if (type == ReportType.MD) {
			
			for (String field: columnNames) {
				result += "*" + field + "*  ";
			}

			result +="\n";
			
			for (Context context:taskList) {
				if (context.getMamaID() == 0) {
					for (String field: context.asArray()) {
						result += "**" + field + "** ";
					}
			
				} else {
					for (String field: context.asArray()) {
						result += field + " ";
					}
				}
				
				result += "\n";
				
			}
		}

		try {
			BufferedWriter buffer = new BufferedWriter(new FileWriter(path));
			buffer.write(result);
			buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		return 0;
	}

}
