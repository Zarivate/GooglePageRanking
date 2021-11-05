import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Project Description:
 * This program recreates Google's original page ranking algorithm used in their search engine.
 * The program utilizes HashMaps and HashSets to properly store and return the best likely
 * result for whatever the user typed in.
 *
 *@Author Ivan Zarate
 *CSCI 340
 *Section 001*
 *Assignment 5 Page Rank
 *Known Bugs: None
 */

public class PageRank {

    // Declares a global constant to be used for later
    private static final double DCONS = .15;

    // String will hold the link to the site while int will be it's ranking
    private static HashMap<String, Site> fileContents = new HashMap<>();

    // Global HashMap to hold all the sites that have the words looked for by the user
    private static HashMap<String, Site> relevantSites = new HashMap<>();

    // Variable to hold the total number of corresponding results from user input
    private static int numberHits;

    /**
     * Inner object class called "Site" that holds all the info of a page besides the main link to it.
     * Is called whenever a new page is reached in the file and stores the appropriate data from the page.
     */

    public static class Site {

        // HashSet to hold all the sites referenced by the page
        HashSet<String> refSites;

        // String to hold the words of each page
        String words;

        // Double to hold the ranking of the page
        private double ranking;

        /**
         * Constructor that builds the actual Site object
         */

        public Site() {

            // Sets the "refSites" variable to a default HashSet
            this.refSites = new HashSet<>();

            // Default sets the ranking variable to 1
            this.ranking = 1;
        }

        /**
         * Method to set the "words" variable equal to what is in the actual Page
         *
         * @param siteWords hold the actual words in the page
         */

        public void setWords(String siteWords) {

            // Sets the "words" variable equal to the parameter sent in
            this.words = siteWords;
        }

        /**
         * Method to add any outgoing links to the "refSites" HashSet in the object
         *
         * @param siteRef is the link to the outgoing site that is sent in as the parameter
         */

        public void setReferences(String siteRef) {

            // Adds the parameter to the string HashSet "refSites"
            refSites.add(siteRef);
        }

        /**
         * Method to get the ranking of the object
         *
         * @return is what gives back the ranking variable of the object
         */

        public double getRanking() {

            // Returns the ranking
            return ranking;
        }

        /**
         * Method to get the sites references by the object/site
         *
         * @return is what gives back the HashSet containing all the referenced sites by the object
         */

        public HashSet<String> getRefSites() {

            // Returns the string HashSet held in the object
            return refSites;
        }

        /**
         * Method to get the words stored in the object
         *
         * @return is what gives back the words held in the object
         */

        public String getWords() {

            // Returns the words held in the object
            return this.words;
        }

        /**
         * Method that sets the default/starting rank of each object after all the pages are calculated.
         * This is because the starting rank is determined by the total number of pages/size of the main HashMap
         */
        public void setDefaultRank() {

            // Sets ranking equal to the new ranking
            this.ranking = ranking / fileContents.size();
        }

        /**
         * Method to update the ranking after the original/default is set
         * @param newVal is what gets set as the ranking
         */

        public void setNewRank(double newVal) {

            // Sets the ranking equal to what was sent in
            this.ranking = newVal;
        }

        /**
         * Method to get the sites a page links to
         * @param link is what's sent in as used for comparison
         * @return is what gives back our List with all the sites referenced by the link
         */

        public List<Site> getLinksTo(String link) {

            // Create a List that will hold the Sites that the sent in link links to
            List<Site> pageRefs = new ArrayList<>();

            // For each loop to go through every Site and see whether it finds the sent in link
            for (Map.Entry<String, Site> position : fileContents.entrySet()) {
                if ((fileContents.get(position.getKey()).getRefSites()).contains(link)) {

                    // If so it adds it to the List of Sites
                    pageRefs.add(position.getValue());
                }
            }

            // Returns our created List of Sites
            return pageRefs;
        }
    }

    /**
     * Method to fill the main HashMap using the file chosen by the user
     *
     * @param fileName is the file chosen by the user and is what's sent in to be processed through
     * @throws IOException is what's thrown if an error occurs while processing the file's contents
     */

    private static void fillMapsAndSets(File fileName) throws IOException {

        // String variable to hold the currentLine the scanner is at when processing the file
        String currentLine = "";

        // Scanner to go through the sent in file
        Scanner reader = new Scanner(fileName);

        // Set the variable "currentLine" equal to the next line in the file
        currentLine = reader.nextLine();

        // While loop that continues so long as the file has content to read
        while (reader.hasNextLine()) {

            // String variable to hold the link to the site/page that will be used throughout loop
            String currentPageLink;

            // Statement to check to see if the "currentLine" variable is equal to "PAGE"
            if (currentLine.equals("PAGE")) {

                // If the current line is at "PAGE" that means that the next line is the link to it
                // so a string variable is created to hold it
                currentPageLink = reader.nextLine();

                // A site object is created that will be used later to store the appropriate variables
                Site currentSite = new Site();

                // The next line should be the content of the site/Page so the "currentLine" variable is set equal to it
                // alongside setting all the letters to lowercase
                currentLine = reader.nextLine().toLowerCase();

                // Content of the page is added to the site object
                currentSite.setWords(currentLine);


                // Set the current line to the next line in the file which should be the start of the outgoing links
                currentLine = reader.nextLine();

                // While loop that continues so long as there is still content in the file and the current
                // line isn't the start of another page
                while (!currentLine.equals("PAGE") && reader.hasNextLine()) {

                    // Sets the references of the site
                    currentSite.setReferences(currentLine);

                    // Moves to the next line
                    currentLine = reader.nextLine();
                }

                // Adds the created object "currentSite" and it's corresponding string link to the main HashMap
                fileContents.put(currentPageLink, currentSite);

                // For loop to set the default rank of each page
                for (String currentKey : fileContents.keySet()) {
                        fileContents.get(currentKey).setDefaultRank();
                }
            }
        }
    }

    /**
     * Method to search for the word(s) the user typed in. As the order of the words don't matter but them
     * all being there does, tries to find every word the user typed in before confirming a "hit". A hit being
     * how many times the word was found in a page.
     *
     * @param searchTerm is the sent in parameter which holds the word(s) the user typed in to be searched for
     * @return gives back the total number of times the word(s) were found in the file
     */

    public static int search(String searchTerm) {

        // Considers the possibility of the user typing in more than one word and splits it up accordingly and
        // stores them in a string array called "splitLine"
        String[] splitLine = searchTerm.toLowerCase().split("\\s+|\\.|,");

        // Nested for loop to check to see if every word the user typed in appears in the given page
        for (String currentKey : fileContents.keySet()) {

            // Boolean to be used to see if a "hit" can be confirmed
            Boolean allExist = true;

            // Inner for loop that goes through every word in the "splitLine" array
            for (String currentWord : splitLine) {

                // If it finds that even one word doesn't exist in the page, sets boolean to false
                if (!fileContents.get(currentKey).getWords().contains(currentWord)) {

                    // Sets boolean to false
                    allExist = false;
                }
            }
            // Checks to see if the boolean is true, if so means all words were found in the page and increments variable "numberHits"
            // while also adding the site to our HashMap that has all the sites where it appeared in
            if (allExist.equals(true)) {
                numberHits++;
                relevantSites.put(currentKey,fileContents.get(currentKey));
            }
        }

        // Returns variable "numberHits"
        return numberHits;
    }

    /**
     * Method to calculate the appropriate ranking for each site. Utilizes the algorithm,
     * pageRank = ((1-d)/totalPages) + (d * SIGMA(rankOfReferencingPage /totalExternalLinks )
     */
    private static void calculate() {

        // For each loop that
        for (Map.Entry<String, Site> position : fileContents.entrySet()) {
            fileContents.get(position.getKey()).setNewRank((1.0 - DCONS) / fileContents.size());
        }

        for (Map.Entry<String, Site> position : fileContents.entrySet()) {
            double siteRefs = 0.0;
            List<Site> referSites = position.getValue().getLinksTo(position.getKey());
            for (int y = 0; y < referSites.size(); y++) {
                siteRefs += (fileContents.get(position.getKey()).getRanking() / referSites.get(y).getRefSites().size());
            }

            // Double variable to hold the new rank of the site
            double temp = ((1.0 - DCONS) / fileContents.size() + DCONS * (siteRefs));

            // Updates the rank of the site
            fileContents.get(position.getKey()).setNewRank(temp);
        }
    }

    /**
     * Method to print the results. Utilizes TreeMaps and the global HashMap that contains all the
     * sites and objects relevant to what the user typed in.
     */

    private static void printResults() {

        // DecimalFormat object to make it so all values printed out have the same number of decimals
        DecimalFormat rankFormatter = new DecimalFormat("###.#########");

        // Counter variable to be used to see if printing should be stopped later
        int counter = 0;

        // Create TreeMap object to hold the correct sorted values  to be printed later
        TreeMap<String, Double> sortedOne = new TreeMap<>();

        // For each loop that fills the TreeMap created above with all the variables of the "relevantSites" global HashMap
        for (String example: relevantSites.keySet()) {
            sortedOne.put(example,relevantSites.get(example).getRanking());
        }

        // Another TreeMap is created by calling the method that sorts the TreeMap values in ascending order
        TreeMap<String,Double> name = (TreeMap<String, Double>) sortByValues(sortedOne);


        // Loop that continues so long as results don't exceed 20 or the size of the sorted TreeMap
        while (counter < 20 && counter < sortedOne.size()) {

            // As we want the results in descending order, just have to reverse the order of the TreeMap
            for (String key : name.descendingKeySet()) {
                System.out.println(rankFormatter.format(fileContents.get(key).getRanking()) + " " + key);
                counter++;
            }
        }
    }

    /**
     * Method that sorts a generic map given to it in ascending order via it's value. Used
     * when creating a map with the correct ordered values to print out.
     * @param map is a generic map meant to hold some type of map, be it HashMap, TreeMap, etc
     * @param <K> is a generic key meant to hold some key
     * @param <V> is a generic value meant to hold some sort of value
     * @return is what gives back the new ordered Map
     */

    private static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {

        Comparator<K> valueComparator = new Comparator<K>() {

                    // Method to compare all the values and set them in ascending order
                    public int compare(K k1, K k2) {

                        // Compares first value to second
                        int compare = map.get(k1).compareTo(map.get(k2));

                        // Sorts them accordingly depending on whether they're bigger or smaller than one another
                        if (compare == 0)
                            return 1;
                        else
                            return compare;
                    }
                };

        // Map to be sent back is created
        Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);

        // Place all the pairs found in order into the map created above
        sortedByValues.putAll(map);

        // Sends it back
        return sortedByValues;
    }

    /**
     * Main method to that asks the user for a file to process and what the user would like to search
     * @param args
     * @throws IOException is what is thrown if the user types in a file that doesn't exist
     */
    public static void main(String[] args) throws IOException {

        // Scanner to use for the name of the file the user types in
        Scanner input = new Scanner(System.in);

        // Scanner to use for the word(s) the user might wanna look up
        Scanner userWords = new Scanner(System.in);

        // String to hold the name of the file chosen by the user to use in program
        String fileChosen;

        // Keeps asking the user for name of file until acceptable choice found
        do  {
            System.out.println("Please enter the name of the file you want to read in. The options are either Simple.txt or CS.txt");

            // Saves whatever the user typed in as a string while removing any whitespace at the end
            fileChosen = input.next().trim();
        } while (!(fileChosen.equals("CS.txt") || fileChosen.equals("Simple.txt")));

        // Creates a file object using the string created above
        File inputFile = new File(fileChosen);

        // Calls method to fill the appropriate fields to be searched later
        fillMapsAndSets(inputFile);

        // Calls the calculate method to update the rankings
        calculate();

        // Asks user for word(s) to search for
        System.out.println("Enter your search terms");

        // Saves user input as variable "searchTerms"
        String searchTerms = userWords.nextLine();

        // Sends user input saved as String above to the "search" method
        search(searchTerms);

        // Prints out the following line alongside how many times it found the word(s)
        System.out.println("The number of hits was: " + numberHits);

        // Calls method to print out the results
        printResults();
    }
}

