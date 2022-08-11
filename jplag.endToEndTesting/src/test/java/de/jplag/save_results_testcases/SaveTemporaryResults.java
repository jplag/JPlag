package de.jplag.save_results_testcases;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

import de.jplag.end_to_end_testing.helper.JsonHelper;
import de.jplag.end_to_end_testing.mapper.LanguageToPathMapper;
import de.jplag.end_to_end_testing.model.JsonModel;
import de.jplag.end_to_end_testing.model.ResultModel;
import de.jplag.options.LanguageOption;

// to avoid running these tests during the maven build, this class should not end with "Test".
class SaveTemporaryResults {

    /**
     * only for java results
     * @throws StreamWriteException Intermediate base class for all read-side streaming processing problems,
     * includingparsing and input value coercion problems.
     * @throws DatabindException Intermediate base class for all databind level processing problems, asdistinct from
     * stream-level problems or I/O issues below.
     * @throws IOException Signals that an I/O exception of some sort has occurred. Thisclass is the general class of
     * exceptions produced by failed orinterrupted I/O operations.
     */
    @Disabled
    public void SaveJavaResults() throws StreamReadException, DatabindException, IOException {
        insertNewTestResultsIntoJsonStore(LanguageOption.JAVA);
    }

    /**
     * stores the created temporary json results in the current result file. It should be kept in mind that the results are
     * stored only for the specific languages.
     * @param languageOption the language option to which the results of the tests should be saved
     * @throws StreamWriteException Intermediate base class for all read-side streaming processing problems,
     * includingparsing and input value coercion problems.
     * @throws DatabindException Intermediate base class for all databind level processing problems, asdistinct from
     * stream-level problems or I/O issues below.
     * @throws IOException Signals that an I/O exception of some sort has occurred. Thisclass is the general class of
     * exceptions produced by failed orinterrupted I/O operations.
     */
    private void insertNewTestResultsIntoJsonStore(LanguageOption languageOption) throws StreamReadException, DatabindException, IOException {
        // load the current stored values for the test cases
        Path resultJsonPath = LanguageToPathMapper.getTestResultPathFromLanguageOption(languageOption);
        List<JsonModel> oldJsonModelList = JsonHelper.getJsonModelListFromPath(resultJsonPath);
        // select all temporary stored results
        File[] fileArrayFromTemporaryResultPath = LanguageToPathMapper.getTemporaryResultPathFromLanguageOption(languageOption).toFile().listFiles();
        // directory structure with ResultModels mapped to functionName
        List<JsonModel> newJsonModelList = new ArrayList<JsonModel>();

        for (File directory : fileArrayFromTemporaryResultPath) {
            ResultModel[] functionResultModelArray = new ResultModel[directory.listFiles().length];
            for (int counter = 0; counter < directory.listFiles().length; counter++) {
                functionResultModelArray[counter] = JsonHelper.getResultModelFromPath(directory.listFiles()[counter]);
            }
            newJsonModelList.add(new JsonModel(directory.getName(), functionResultModelArray));
        }

        ArrayList<JsonModel> intersectedResultList = getNewResultJsonList(oldJsonModelList, newJsonModelList);

        JsonHelper.writeJsonModelsToJsonFile(intersectedResultList, resultJsonPath);

        assertTrue(true);
    }

    /**
     * compare if elements of the old list have been changed and compile the intersection of the result elements
     * @param oldResults old list of test results
     * @param newResults new list of test results
     * @return intersection of the result lists
     */
    private ArrayList<JsonModel> getNewResultJsonList(List<JsonModel> oldResults, List<JsonModel> newResults) {
        var returnModel = new ArrayList<JsonModel>();
        // the new list is necessary to be able to remove the elements
        LinkedList<JsonModel> oldResultLinkedList = new LinkedList<JsonModel>();
        if(oldResults != null)
        {
        	oldResultLinkedList.addAll(oldResultLinkedList);
        }

        // if the current result file is empfy no need to compaire the files
        if (oldResultLinkedList.size() == 0) {
            returnModel.addAll(newResults);
            return returnModel;
        }
        for (int counter = 0; counter < oldResultLinkedList.size(); counter++) {
            var oldJsonResult = oldResultLinkedList.get(counter);
            JsonModel duplicate = newResults.stream()
                    .filter(newResultFilterObject -> newResultFilterObject.getFunctionName().equals(oldJsonResult.getFunctionName())).findFirst()
                    .orElse(null);

            if (duplicate != null) {
                oldResultLinkedList.remove(counter);
                counter--;
            }
        }
        returnModel.addAll(oldResultLinkedList);
        returnModel.addAll(newResults);

        return returnModel;
    }
}
