# Complete System e2e Tests

The e2e tests are executed by the [complete e2e tests workflow](../../../.github/workflows/complete-e2e.yml) and are meant to check the entire process from building and executing JPlag to viewing the report in the report viewer.
The tests get run on 3 different operating systems: Windows, Ubuntu and MacOS.

## Structure

First in the `build_jar` job the JPlag jar is built.

Then in the `run_jplag` job the JPlag jar is executed with the test data. Here using matrix, JPlag is run for each dataset on each operating system.

Finally in the `e2e_test` job the playwright tests specified here are run. The tests are run on each operating system.

### Open Comparison Tests

The `OpenComparisonTest.spec.ts` test, loads each of the specified reports and tries to open its top comparison. This is done to ensure that basic functionality of JPlag is working. 
We test that the most used languages are working correctly and that the report viewer does not throw an error opening them. We also test that all ways to give files as single files or folders are exported into their respective reports.

### Other Tests

The other tests are testing the functionality of the report viewer. Each of them tests one view and makes sure all the features are working correctly.

## Running the tests locally

1) To run the tests locally get the report files of the datasets from the [GitHub](../../../.github/workflows/files/) and execute JPlag on its contents.
2) Build the report viewer using `npm run build`
3) Run the e2e tests using `npm run test:e2e`

## Adding new tests

If you want to add new tests we suggest doing the following tests:

1) Create a dataset you can upload to GitHub
   - Copy the zip of the dataset into [the workflow files folder](../../../.github/workflows/files/)
   - Execute it on your device, so you can test your new test locally
   - If you want to add the dataset to `OpenComparisonTest.spec.ts` make sure there is a clear top comparison and you do not have multiple comparisons with the same percentage as the top comparison
2) Add the test to the matrix in the [complete e2e tests workflow](../../../.github/workflows/complete-e2e.yml)
   - zip: The name of the zip file in the files folder
   - name: The name of the dataset. This name should be unique
   - folder: This is the main folder of the dataset, that gets passed to JPlag as a positional argument
   - language: The language JPlag should use. This should be the same name passed to the `-l` parameter
   - cliArgs: Additional arguments to pass to JPlag. This could be used to specify basecode or give JPlag more folders over `--new`/`--old`

3) Add the test to the playwright e2e tests.
   - Adding a test to `OpenComparisonTest`:
     - Add the dataset name to the `datasets` array
     - Specify the name of the report that should be opened. They follow the pattern `DATASET_NAME-report.jplag`
     - Specify the names of the submissions of the top comparisons. These are given as regexes
   - Adding a completely new Test:
     - Create a new file in this folder with the file ending `.spec.ts`
     - Add a new test according to the playwright documentation (examples are in the other tests)
     - The test should start like this
       ```typescript
       test('Name of the test', async ({ page }) => {
         await uploadFile('YOUR_DATASET_NAME-report.jplag', page)
         // Your test code
       });
       ```
       This will start you on the overview page with the dataset loaded

4) Run the tests locally to make sure they are working
