import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

import binarysearchtree.BinarySearchTree;
import enums.SystemObjectTypes;

public class SystemClass 
{
    // System Objects: Lawsuit, citizen, lawyer, lawoffice owner, judge, government official
    private List<BinarySearchTree<AbstractSystemObject>> systemObjects;

    // sistemObjects olusturulurken cmk avukatlari da bir taraftan buraya eklenecek, boolean degere gore
    private Queue<Lawyer> stateAttorneyReferences;
    
    // sistemObjects olusturulurken lawsuit'ler, date'e gore priority queue'e eklenecek
    private PriorityQueue<Lawsuit> lawsuits;

    // Owner, is ilani olustururken, bir referans da buraya eklenecek
    private Queue<LawOffice.JobAdvertisement> jobAdvertisementsReferences;


    public SystemClass() 
    {
        // Her index'e kendilerine karsilik gelen BST'yi eklemek istedim ama BST classi comparable
        // generic aldigi icin beceremedim.
        systemObjects = new ArrayList<>(AbstractSystemObject.NUMBER_OF_SYSTEM_OBJECTS);
        for (int i = 0; i < AbstractSystemObject.NUMBER_OF_SYSTEM_OBJECTS; i++)
            systemObjects.add(new BinarySearchTree<>());

        stateAttorneyReferences = new LinkedList<>();
        jobAdvertisementsReferences = new LinkedList<>();
        
        // Lambda method
        lawsuits = new PriorityQueue<>((lawsuit1, lawsuit2) -> lawsuit1.getDate().compareTo(lawsuit2.getDate()));

        //  Anonymous class
        // lawsuits = new PriorityQueue<>(new Comparator<Lawsuit>() {
        //     @Override
        //     public int compare(Lawsuit lawsuit1, Lawsuit lawsuit2) {
        //         return lawsuit1.getDate().compareTo(lawsuit2.getDate());
        //     }
        // });
        
        
    }
    
    
    /**
     * It adds the given system object to the appropriate Binary Search Tree
     * 
     * @param systemObject The object to be registered.
     */
    public void registerSystemObject(AbstractSystemObject systemObject)
    {
        // SystemObjectsCodes'a gore bst'yi getir ve objeyi ekle.
        SystemObjectTypes systemObjectType = findSystemObjectType(systemObject.getId());
        // Codes starts from 1.
        int index = systemObjectType.getSystemObjectCode() - 1;

        if (systemObjectType == SystemObjectTypes.LAWSUIT)
        {
            lawsuits.add((Lawsuit)systemObject);
        }
        if (systemObjectType == SystemObjectTypes.LAWYER && ((Lawyer) systemObject).getStateAttorney())
        {
            stateAttorneyReferences.add((Lawyer) systemObject);
        }
        systemObjects.get(index).add(systemObject);  
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
        // Code'a gore arraylist'ten bst'yi get yap.
        // Anonymous class yaratip oradan find'a pass edip id compare ederek, aradigimiz objeyi buluruz.
        return systemObjects.get(index).find(new AbstractSystemObject(id) {
            @Override
            public int getId() {
                return id;
            }
        });
    }

    // Id'den hangi type system object oldugunu bul
    public static SystemObjectTypes findSystemObjectType(int id)
    {
        // Error check gelebilir

        int code = id / (int) Math.pow(10, AbstractSystemObject.ID_LENGTH - 1);
        int index = code - 1;
        // Kriptik version
        // return SystemObjectTypes.values()[index];

        // -- Daha az kriptik versiyonu --
        switch (SystemObjectTypes.values()[index]) {
            case LAWSUIT:
                return SystemObjectTypes.LAWSUIT; 
            case CITIZEN:
                return SystemObjectTypes.CITIZEN;
            case LAWYER:
                return SystemObjectTypes.LAWYER;
            case LAWOFFICE_OWNER:
                return SystemObjectTypes.LAWOFFICE_OWNER;
            case JUDGE:
                return SystemObjectTypes.JUDGE;
            case GOVERNMENT_OFFICIAL:
                return SystemObjectTypes.GOVERNMENT_OFFICIAL;
            default:
                throw new IllegalArgumentException("Invalid system object type.");
        }
    }

    // Helpers

    /**
     * This function adds a Lawyer object to the stateAttorneyReferences ArrayList.
     * 
     * @param stateAttorney The state attorney to add to the list of state attorneys.
     */
    public void addStateAttorney(Lawyer stateAttorney)
    {
        stateAttorneyReferences.add(stateAttorney);
    }


    /**
     * This function adds a lawsuit to the list of lawsuits.
     * 
     * @param lawsuit The lawsuit to add to the list of lawsuits.
     */
    public void addLawsuit(Lawsuit lawsuit)
    {
        lawsuits.add(lawsuit);
    }

    /**
     * Adds a job advertisement to the list of job advertisements.
     * 
     * @param jobAdvertisement The job advertisement to be added to the list of job advertisements.
     */
    public void addJobAdvertisement(LawOffice.JobAdvertisement jobAdvertisement)
    {
        jobAdvertisementsReferences.add(jobAdvertisement);
    }

    // Getters
    public List<BinarySearchTree<AbstractSystemObject>> getSystemObjects()
    {
        return systemObjects;
    }

    public Queue<Lawyer> getStateAttorneyReferences() {
        return stateAttorneyReferences;
    }

    public PriorityQueue<Lawsuit> getLawsuits() {
        return lawsuits;
    }

    public Queue<LawOffice.JobAdvertisement> getJobAdvertisementsReferences() {
        return jobAdvertisementsReferences;
    }
}
