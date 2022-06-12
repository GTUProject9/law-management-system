import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;
import java.util.TreeSet;

import enums.SystemObjectTypes;

public class SystemClass 
{
    // System Objects: Lawsuit, citizen, lawyer, lawoffice owner, judge, government official
    private List<TreeMap<Integer, AbstractSystemObject>> systemObjects;

    // sistemObjects olusturulurken cmk avukatlari da bir taraftan buraya eklenecek, boolean degere gore
    private Queue<Lawyer> stateAttorneyReferences;

    private Queue<Lawyer> stateAttorneyApplicants;

    private List<PriorityQueue<Lawsuit>> lawsuitsByDate;

    // Owner, is ilani olustururken, bir referans da buraya eklenecek
    private ArrayList<LawOffice.JobAdvertisement> jobAdvertisementsReferences;


    private int lawyerCounter = 0;
    private int lawsuitCounter = 0;

    private static final int JUDGE_NUMBER = 10;

    /**
     * SystemClass constructor.
     */
    public SystemClass() 
    {
        systemObjects = new ArrayList<>();
        for (int i = 0; i < SystemObjectTypes.values().length; i++) {
            systemObjects.add(new TreeMap<>());
        }

        stateAttorneyReferences = new LinkedList<>();
        jobAdvertisementsReferences = new ArrayList<>();
        stateAttorneyApplicants = new LinkedList<>();
        
        lawsuitsByDate = new ArrayList<>(JUDGE_NUMBER);
        for (int i = 0; i < JUDGE_NUMBER; i++)
        {
            lawsuitsByDate.add(new PriorityQueue<>((lawsuit1, lawsuit2) -> lawsuit1.getDate().compareTo(lawsuit2.getDate())));
        }
    }
    
    // ============ TEMEL SISTEM CLASSI METHODLARI ============
    /**
     * It adds the given system object to the appropriate TreeMap
     * 
     * @param systemObject The object to be registered.
     */
    public void registerSystemObject(AbstractSystemObject systemObject)
    {
        SystemObjectTypes systemObjectType = findSystemObjectType(systemObject.getId());
        // Codes starts from 1.
        int index = systemObjectType.getSystemObjectCode() - 1;

        if (systemObjectType == SystemObjectTypes.LAWSUIT)
        {
            Lawsuit lawsuit = (Lawsuit) systemObject;
            if (lawsuit.getJudge() != null)
            {
                lawsuitsByDate.get(lawsuit.getJudge() % 10 - 1).add(lawsuit);
            }
        }
        if (systemObjectType == SystemObjectTypes.LAWYER && ((Lawyer) systemObject).getStateAttorney())
        {
            stateAttorneyReferences.offer((Lawyer) systemObject);
        }
        if (systemObjectType == SystemObjectTypes.LAWYER)
        {
            lawyerCounter++;
        }
        if (systemObjectType == SystemObjectTypes.LAWSUIT)
        {
            lawsuitCounter++;
        }
        systemObjects.get(index).put(systemObject.getId(), systemObject);  
    }

    /**
     * It takes a system object as a parameter, finds the type of the system object, and adds it to the
     * corresponding MapTree
     * 
     * @param systemObject The object to be deleted.
     */
    public void deleteSystemObject(AbstractSystemObject systemObject)
    {
        SystemObjectTypes systemObjectType = findSystemObjectType(systemObject.getId());
        // Codes starts from 1.
        int index = systemObjectType.getSystemObjectCode() - 1;

        if (systemObjectType == SystemObjectTypes.LAWSUIT)
        {
            Lawsuit lawsuit = (Lawsuit) systemObject;
            if (lawsuit.getJudge() != null)
            {
                lawsuitsByDate.get(lawsuit.getJudge() % 10 - 1).remove(lawsuit);
            }
        }
        if (systemObjectType == SystemObjectTypes.LAWYER && ((Lawyer) systemObject).getStateAttorney())
        {
            stateAttorneyReferences.add((Lawyer) systemObject);
        }
        if (systemObjectType == SystemObjectTypes.LAWYER)
        {
            lawyerCounter++;
        }
        systemObjects.get(index).put(systemObject.getId(), systemObject);  
    }
    
    /**
     * Find the correct Binary Search Tree, then get the system object.
     * 
     * @param id The id of the system object.
     * @return AbstractSystemObject
     */
    public AbstractSystemObject getSystemObject(int id)
    {
        SystemObjectTypes systemObjectCode = findSystemObjectType(id);
        // Codes starts from 1.
        int index = systemObjectCode.getSystemObjectCode() - 1;
        return systemObjects.get(index).get(id);
    }


    /**
     * This function adds a Lawyer object to the stateAttorneyReferences ArrayList.
     * 
     * @param stateAttorney The state attorney to add to the list of state attorneys.
     */
    public void addStateAttorney(Lawyer stateAttorney)
    {
        stateAttorneyReferences.offer(stateAttorney);
    }

    
    // ============ HELPERS ============
    /**
     * It takes an integer as an argument and returns the corresponding SystemObjectType
     * 
     * @param id The id of the object.
     * @return SystemObjectTypes enum
     */
    public static SystemObjectTypes findSystemObjectType(int id)
    {
        int code = id / (int) Math.pow(10, AbstractSystemObject.ID_LENGTH - 1);
        int index = code - 1;
        return SystemObjectTypes.values()[index];
    }

    /**
     * "Check if the password of the citizen with the given id is equal to the given password."
     * 
     * The function is called "checkPassword" and it takes two parameters: an integer and a string. The
     * integer is the id of the citizen and the string is the password. The function returns a boolean
     * value
     * 
     * @param id The id of the citizen
     * @param password The password to check.
     * @return A boolean value.
     */
    public boolean checkPassword(int id, String password)
    {
        Citizen citizen = (Citizen) getSystemObject(id);
        if (citizen == null)
            return false;
        
        return citizen.getPassword().equals(password);
    }


    // ============ LAW OFFICE OWNER ============
    /**
     * Adds a job advertisement to the list of job advertisements.
     * 
     * @param jobAdvertisement The job advertisement to be added to the list of job advertisements.
     */
    public void addJobAdvertisement(LawOffice.JobAdvertisement jobAdvertisement)
    {
        jobAdvertisementsReferences.add(jobAdvertisement);
    }


    // ============ GOVERNMENT OFFICAL ============
    /**
     * > This function adds a lawyer to the system
     * 
     * @param lawyer The lawyer object to be added to the system.
     */
    public void addLawyer(Lawyer lawyer)
    {
        int initialId = SystemObjectCreator.createInitialId(SystemObjectTypes.LAWYER.getSystemObjectCode());
        lawyer.setId(initialId + lawyerCounter);
        registerSystemObject(lawyer);
    }

    /**
     * Return the first element in the queue and remove it from the queue.
     * 
     * @return The first element in the queue.
     */
    public Lawyer peekStateAttorneyApplicant() {
        return stateAttorneyApplicants.peek();
    }

    public Lawyer pollStateAttorney() {
        return stateAttorneyApplicants.poll();
    }
    // Print state attorney applicants
    public void printStateAttorneyApplicants() {
        int i = 0;
        System.out.println("State attorney applicants: ");
        for (Lawyer lawyer : stateAttorneyApplicants) {
            System.out.println(i + ". " + lawyer.getId());
        }
        System.out.println();
    }

    public Lawyer getStateAttorney() {
        return stateAttorneyReferences.poll();
    }

    // ============ JUDGE ============
    /**
     * "Get the highest priority lawsuit for a given judge."
     * 
     * The function is called getHighestPriorityLawsuit and it takes one parameter, judgeId
     * 
     * @param judgeId The ID of the judge who is currently handling the case.
     * @return The highest priority lawsuit for a given judge.
     */
    public Lawsuit getHighestPriorityLawsuit(int judgeId)
    {
        return lawsuitsByDate.get(judgeId % 10 - 1).poll();
    }
    

    /**
     * This function adds a lawsuit to the list of lawsuits.
     * 
     * @param lawsuit The lawsuit to add to the list of lawsuits.
     */
    public void addLawsuit(Lawsuit lawsuit)
    {
        int initialId = SystemObjectCreator.createInitialId(SystemObjectTypes.LAWSUIT.getSystemObjectCode());
        lawsuit.setId(initialId + lawsuitCounter);
        registerSystemObject(lawsuit);
    }

    /**
     * This method, set a lawsuit to the priority queue after assignation lawsuit to a judge.
     * @param lawsuit
     */
    public void addLawsuitByDate(Lawsuit lawsuit)
    {
        lawsuitsByDate.get(lawsuit.getJudge() % 10 - 1).add(lawsuit);
    }

    // ============ CITIZEN ============
    /**
     * "Get all the lawyers who accept lawsuits."
     * 
     * The function is a bit long, but it's not complicated. It's just a loop that iterates over all
     * the lawyers and prints the ones who accept lawsuits
     */
    public void getLawsuitAcceptingLawyers()
    {
        int index = SystemObjectTypes.LAWYER.getSystemObjectCode() - 1;
        Iterator<AbstractSystemObject> iterator = systemObjects.get(index).values().iterator();
        int i = 1;
        while (iterator.hasNext())
        {
            Lawyer lawyer = (Lawyer) iterator.next();
            if (lawyer.acceptsLawsuits())
            {
                System.out.println(i + ". " + lawyer + "\n");
                i++;
            }
        }
    }

    /**
     * > Assigns a lawyer to a lawsuit, if there is one available
     * 
     * @param lawsuitId The id of the lawsuit that needs to be assigned to a lawyer.
     * @return The id of the lawyer assigned to the lawsuit.
     */
    public Integer assignStateAttorney(int lawsuitId)
    {   
        Lawyer lawyer = stateAttorneyReferences.poll();
        if (lawyer == null)
            return null;
        lawyer.addLawsuit(lawsuitId);

        stateAttorneyReferences.offer(lawyer);
        return lawyer.getId();
    }

    // ============ LAWYER ============
    /**
     * Adds a state attorney to the queue of state attorney applicants.
     * 
     * @param stateAttorney The state attorney to add to the queue.
     */
    public void addStateAttorneyApplicant(Lawyer stateAttorney)
    {
        stateAttorneyApplicants.offer(stateAttorney);
    }

    // get citizen
    /**
     * This function returns a Citizen object with the given id.
     * 
     * @param id The id of the citizen you want to get.
     * @return A Citizen object.
     */
    public Citizen getCitizen(int id)
    {
        return (Citizen) getSystemObject(id);
    }
    
    // get lawsuit
    /**
     * This function returns a Lawsuit object with the given id.
     * 
     * @param id The id of the lawsuit you want to get.
     * @return A Lawsuit object.
     */
    public Lawsuit getLawsuit(int id)
    {
        return (Lawsuit) getSystemObject(id);
    }

    /**
     * This function displays the job advertisements.
     */
    public void displayJobAdvertisements()
    {
        System.out.println(jobAdvertisementsReferences);
    }
    
    /**
     * This function returns the employer id of the job advertisement at the given index.
     * 
     * @param index the index of the job advertisement in the list of job advertisements.
     * @return The employer id of the job advertisement at the given index.
     */
    public int getEmployerId(int index)
    {
        return jobAdvertisementsReferences.get(index).getOwnerId();
    }
    
    /**
     * "Add a job application to the office of the owner of the job application."
     * 
     * The first line of the function is a comment. Comments are ignored by the compiler. They are used
     * to explain what the code does
     * 
     * @param jobApplication The job application to add.
     */
    public void addJobApplication(Lawyer.JobApplication jobApplication)
    {
        LawOfficeOwner owner = (LawOfficeOwner) getSystemObject(jobApplication.getOwnerId());
        owner.getOffice().addJobApplication(jobApplication);
    }


    public void printLawsuit(Lawsuit lawsuitItem){

        System.out.println("\n\n***********************************");
        System.out.println("Id\t" + lawsuitItem.id);
        System.out.println("Status\t" + lawsuitItem.getStatus());
        System.out.println("Suing Citizen\t" + lawsuitItem.getSuingCitizen());
        System.out.println("Sued Citizen\t" + lawsuitItem.getSuedCitizen());
        System.out.println("Suing Lawyer\t" + lawsuitItem.getSuingLawyer());
        System.out.println("Sued Lawyer\t" + lawsuitItem.getSuedLawyer());
        System.out.println("Suing Defence\t" + lawsuitItem.getSuingDefence());
        System.out.println("Sued Defence\t" + lawsuitItem.getSuedDefence());
        System.out.println("Case File\t" + lawsuitItem.getCaseFile());
        System.out.println("Law Suit Type\t" + lawsuitItem.getLawsuitType());
        System.out.println("Judge\t" + lawsuitItem.getJudge());
        System.out.println("Counrt Records\t" + lawsuitItem.getCourtRecords());

    }

    /**
     * accessToTheArchive
     *
     */
    public void accessToTheArchive() {
        TreeMap<Integer, AbstractSystemObject> lawsuits = systemObjects.get(0);
        for (AbstractSystemObject item : lawsuits.values()) {
            Lawsuit lawsuitItem = (Lawsuit) item;
            printLawsuit(lawsuitItem);
        }
    }

    public void addDefenseToTheLawsuit(TreeSet<Integer> continuingLawsuits){
        TreeMap<Integer, AbstractSystemObject> lawsuits = systemObjects.get(0);
        System.out.println("Please Insert Case Id For Adding Defense");

        for (AbstractSystemObject item : lawsuits.values()) {
            if(continuingLawsuits.contains(item)) {
                printLawsuit((Lawsuit) lawsuits.get(item));
            }
        }
        int choice = Utils.readIntegerInput();
        Lawsuit lawsuitForAddingDefence = (Lawsuit) lawsuits.get(choice);
        System.out.println("Enter Your Defence");
        String defence = Utils.readStringInput();
        //bu savunma üsteki davaya nasıl birleştirilecek ?
    }

    // ============ LAW OFFICE OWNER ============
    /**
     * This function returns a lawyer object with the given id.
     * 
     * @param id The id of the lawyer you want to get.
     * @return A lawyer object
     */
    public Lawyer getLawyer(int id)
    {
        return (Lawyer) getSystemObject(id);
    }
    
    // ============ GETTERS ============
    /**
     * This function returns a list of TreeMaps of Integers and AbstractSystemObjects.
     * 
     * @return A list of TreeMaps of Integers and AbstractSystemObjects.
     */
    public List<TreeMap<Integer, AbstractSystemObject>> getSystemObjects()
    {
        return systemObjects;
    }

    // Gerekli !!
    /**
     * This function returns a queue of lawyers.
     * 
     * @return A queue of lawyers.
     */
    public Queue<Lawyer> getStateAttorneyReferences() {
        return stateAttorneyReferences;
    }

    /**
     * This function returns the job advertisements references
     * 
     * @return The job advertisements references.
     */
    public ArrayList<LawOffice.JobAdvertisement> getJobAdvertisementsReferences() {
        return jobAdvertisementsReferences;
    }

    /**
     * > This function returns a list of priority queues of lawsuits, sorted by date
     * 
     * @return A list of priority queues of lawsuits.
     */
    public List<PriorityQueue<Lawsuit>> getLawsuitsByDate() {
        return lawsuitsByDate;
    } 
}
