package org.activiti.upgrade;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.upgrade.helper.UpgradeUtil;

/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Joram Barrez
 */
public class DataGenerator {
  
  public static void main(String[] args) {
    ProcessEngine processEngine = UpgradeUtil.getProcessEngine();
    createCommonData(processEngine);
    
    // 5.15 specific
    createTaskCategoryData(processEngine);
    createHistoricVariableTimeStoreTestData(processEngine);
  }
  
  private static void createCommonData(ProcessEngine processEngine) {
    // UpgradeTaskOneTest in 5.8
    RuntimeService runtimeService = processEngine.getRuntimeService();
    TaskService taskService = processEngine.getTaskService();

    processEngine.getRepositoryService()
      .createDeployment()
      .name("simpleTaskProcess")
      .addClasspathResource("org/activiti/upgrade/test/UserTaskBeforeTest.testSimplestTask.bpmn20.xml")
      .deploy();

    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("simpleTaskProcess");
    String taskId = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult().getId();
    taskService.complete(taskId);
    
    // UpgradeTaskTwoTest in 5.8
    processEngine.getRepositoryService().createDeployment().name("simpleTaskProcess")
            .addClasspathResource("org/activiti/upgrade/test/UserTaskBeforeTest.testTaskWithExecutionVariables.bpmn20.xml")
            .deploy();

    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("instrument", "trumpet");
    variables.put("player", "gonzo");
    runtimeService.startProcessInstanceByKey("taskWithExecutionVariablesProcess", variables);
  }
  
  private static void createTaskCategoryData(ProcessEngine processEngine) {
  	 RuntimeService runtimeService = processEngine.getRuntimeService();
     TaskService taskService = processEngine.getTaskService();

     processEngine.getRepositoryService()
       .createDeployment()
       .name("simpleTaskProcess")
       .addClasspathResource("org/activiti/upgrade/test/testTaskCategory.bpmn20.xml")
       .deploy();

     // Process instance is started and user task is available
     // The 5.15 upgrade will add the category later after upgrade
     ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("testTaskCategory");
  }
  
  private static void createHistoricVariableTimeStoreTestData(ProcessEngine processEngine) {
  	 processEngine.getRepositoryService().createDeployment().name("simpleTaskProcess")
     	.addClasspathResource("org/activiti/upgrade/test/HistoricVariableTimeStoreTest.bpmn20.xml")
     	.deploy();
  	 
  	 HashMap<String, Object> vars = new HashMap<String, Object>();
  	 vars.put("myVar", "myValue");
  	 processEngine.getRuntimeService().startProcessInstanceByKey("historicVariableTimeStoreTestProcess", vars);
  }

}
