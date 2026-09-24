package de.jplag.emf.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

/**
 * Utility class for the creation of test models.
 */
public class BookStoreFactory {

    private static final String MODEL_NAME = "bookStore.xml";
    private static final String METAMODEL_NAME = "bookStore.ecore";

    /**
     * Generates all models to a given directory.
     * @param filePath is the directory path.
     */
    public static void generateAll(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        BookStoreFactory.createMetamodelAndModelInstance(filePath);
    }

    @SuppressWarnings("unchecked")
    private static void createMetamodelAndModelInstance(String baseBath) {
        /*
         * Create metamodel:
         */
        EcoreFactory theCoreFactory = EcoreFactory.eINSTANCE;

        EClass bookStoreEClass = theCoreFactory.createEClass();
        bookStoreEClass.setName("BookStore");

        EClass bookEClass = theCoreFactory.createEClass();
        bookEClass.setName("Book");

        EPackage bookStoreEPackage = theCoreFactory.createEPackage();
        bookStoreEPackage.setName("BookStorePackage");
        bookStoreEPackage.setNsPrefix("bookStore");
        bookStoreEPackage.setNsURI("http:///com.ibm.dynamic.example.bookstore.ecore");

        EcorePackage theCorePackage = EcorePackage.eINSTANCE;

        EAttribute bookStoreOwner = theCoreFactory.createEAttribute();
        bookStoreOwner.setName("owner");
        bookStoreOwner.setEType(theCorePackage.getEString());
        EAttribute bookStoreLocation = theCoreFactory.createEAttribute();
        bookStoreLocation.setName("location");
        bookStoreLocation.setEType(theCorePackage.getEString());
        EReference bookStore_Books = theCoreFactory.createEReference();
        bookStore_Books.setName("books");
        bookStore_Books.setEType(bookEClass);
        bookStore_Books.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
        bookStore_Books.setContainment(true);

        EAttribute bookName = theCoreFactory.createEAttribute();
        bookName.setName("name");
        bookName.setEType(theCorePackage.getEString());
        EAttribute bookISBN = theCoreFactory.createEAttribute();
        bookISBN.setName("isbn");
        bookISBN.setID(true);
        bookISBN.setEType(theCorePackage.getEInt());

        bookStoreEClass.getEStructuralFeatures().add(bookStoreOwner);
        bookStoreEClass.getEStructuralFeatures().add(bookStoreLocation);
        bookStoreEClass.getEStructuralFeatures().add(bookStore_Books);

        bookEClass.getEStructuralFeatures().add(bookName);
        bookEClass.getEStructuralFeatures().add(bookISBN);

        bookStoreEPackage.getEClassifiers().add(bookStoreEClass);
        bookStoreEPackage.getEClassifiers().add(bookEClass);

        /*
         * Create model instance:
         */
        EFactory bookFactoryInstance = bookStoreEPackage.getEFactoryInstance();

        EObject bookObject = bookFactoryInstance.create(bookEClass);
        EObject bookStoreObject = bookFactoryInstance.create(bookStoreEClass);

        bookStoreObject.eSet(bookStoreOwner, "David Brown");
        bookStoreObject.eSet(bookStoreLocation, "Street#12, Top Town, NY");
        ((List<Object>) bookStoreObject.eGet(bookStore_Books)).add(bookObject);

        bookObject.eSet(bookName, "Harry Potter and the Deathly Hallows");
        bookObject.eSet(bookISBN, 157221);

        /*
         * Save model instance and metamodel:
         */
        persist(baseBath, bookStoreObject, MODEL_NAME, "*");
        persist(baseBath, bookStoreEPackage, METAMODEL_NAME, EcorePackage.eNAME);
    }

    private static void persist(String baseBath, EObject eObject, String name, String extension) {
        ResourceSet metaResourceSet = new ResourceSetImpl();
        metaResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(extension, new XMLResourceFactoryImpl());
        Resource metaResource = metaResourceSet.createResource(URI.createFileURI(baseBath + File.separator + name));
        metaResource.getContents().add(eObject);

        try {
            metaResource.save(null);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
