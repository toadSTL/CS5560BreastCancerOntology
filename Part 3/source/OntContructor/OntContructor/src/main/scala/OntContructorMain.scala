import java.io.{FileOutputStream, OutputStream}

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat
import org.semanticweb.owlapi.model._
import org.semanticweb.owlapi.util.DefaultPrefixManager

import scala.io.Source

/**
  * Created by Mayanka on 11-07-2017.
  */
object OntContructorMain {
  def main(args: Array[String]): Unit = {


    /**
      * File Format
      *
      * FileName : Classes
      * Format : ListOfClassNames
      * Person
      * Ethnicity
      * Gender
      *
      * FileName: SubClasses
      * Format : ClassName,SubClassName
      * Person,Daughter
      * Person,Parent
      * Person,OffSpring
      * Person,Son
      * Person,Mother
      * Person,Father
      *
      * FileName: ObjectProperties
      * Format : ObjectPropertyName, DomainName, RangeName, ObjectPropertyType (Functional-Func, InverseOf-InvOf:ObjectPropertyName)
      * hasGender,Person,Gender,Func
      * hasChild,Person,Person,InvOf:hasParent
      *
      * FileName : DataProperties
      * Format : DataPropertyName, DomainName, RangeDataType
      * fullName,Person,string
      * ID,Person,int
      *
      * FileName : Individuals
      * Format : ClassName, IndividualName
      * Gender,Female
      * Gender,Male
      * Person,Mary
      *
      * FileName : Triplets
      * Format : SubjectIndividual, ObjectPropertyName, SubjectIndividual, PropertyType (Obj/Data)
      * Mary,hasGender,Female,Obj
      * Mary,fullName,MaryJost,Data
      */




    val ONTOLOGYURI="http://www.semanticweb.org/gregbrown/ontologies/2018/11/"

    val manager = OWLManager.createOWLOntologyManager
    //creating ontology manager
    val df = manager.getOWLDataFactory //In order to create objects that represent entities

    val ontology = manager.createOntology(IRI.create(ONTOLOGYURI,"breastCancer#"))
    //Prefix for all the entities
    val pm = new DefaultPrefixManager(null, null, ONTOLOGYURI+"breastCancer#")


    // Declaration Axiom for creating Classes
    val classes=Source.fromFile("data/Classes").getLines()

    classes.foreach(f=>{
      val cls = df.getOWLClass(f, pm)
      val declarationAxiomcls= df.getOWLDeclarationAxiom(cls)
      manager.addAxiom(ontology, declarationAxiomcls)
    })

    // Creating SubClassOfAxiom
    val subClasses=Source.fromFile("data/SubClasses").getLines()

    subClasses.foreach(f=>{
      val farr=f.split(",")
      val cls=df.getOWLClass(farr(0), pm)
      val subCls=df.getOWLClass(farr(1), pm)
      val declarationAxiom = df.getOWLSubClassOfAxiom(subCls, cls)
      manager.addAxiom(ontology, declarationAxiom)
    })




    //Creating Object Properties
    val objprop=Source.fromFile("data/ObjectProperties").getLines()
    objprop.foreach(f=> {
      val farr=f.split(",")
      val domain = df.getOWLClass(farr(1), pm)
      val range = df.getOWLClass(farr(2), pm)
      //Creating Object property ‘hasGender’
      val objpropaxiom = df.getOWLObjectProperty(farr(0), pm)

      val rangeAxiom = df.getOWLObjectPropertyRangeAxiom(objpropaxiom, range)
      val domainAxiom = df.getOWLObjectPropertyDomainAxiom(objpropaxiom, domain)

      //Adding Axioms to ontology
      manager.addAxiom(ontology, rangeAxiom)
      manager.addAxiom(ontology, domainAxiom)
      if(farr(3)=="Func")
        manager.addAxiom(ontology, df.getOWLFunctionalObjectPropertyAxiom(objpropaxiom))
      else if(farr(3).contains("InvOf"))
      {
        val inverse=farr(3).split(":")
        val inverseaxiom = df.getOWLObjectProperty(inverse(1), pm)

        val rangeAxiom = df.getOWLObjectPropertyRangeAxiom(inverseaxiom, domain)
        val domainAxiom = df.getOWLObjectPropertyDomainAxiom(inverseaxiom, range)

        //Adding Axioms to ontology
        manager.addAxiom(ontology, rangeAxiom)
        manager.addAxiom(ontology, domainAxiom)
        manager.addAxiom(ontology, df.getOWLInverseObjectPropertiesAxiom(objpropaxiom, inverseaxiom))
      }

    })


    val dataprop=Source.fromFile("data/DataProperties").getLines()

    dataprop.foreach(f=>{
      val farr=f.split(",")
      val domain=df.getOWLClass(farr(1),pm)
      //  Creating Data Property ‘fullName’
      val fullName = df.getOWLDataProperty(farr(0), pm)
      val domainAxiomfullName = df.getOWLDataPropertyDomainAxiom(fullName, domain)
      manager.addAxiom(ontology, domainAxiomfullName)
      if(farr(2)=="string") {
        //Defining String Datatype
        val stringDatatype = df.getStringOWLDatatype()
        val rangeAxiomfullName = df.getOWLDataPropertyRangeAxiom(fullName, stringDatatype)
        //Adding this Axiom to Ontology
        manager.addAxiom(ontology, rangeAxiomfullName)
      }
      else if(farr(2)=="int")
        {
          //Defining Integer Datatype
          val Datatype = df.getIntegerOWLDatatype()
          val rangeAxiomfullName = df.getOWLDataPropertyRangeAxiom(fullName, Datatype)
          //Adding this Axiom to Ontology
          manager.addAxiom(ontology, rangeAxiomfullName)
        }
    })


    //Creating NamedIndividuals using ClassAssertionAxiom
    val individuals=Source.fromFile("data/Individuals").getLines()

    individuals.foreach(f=>{
      val farr=f.split(",")
      val cls=df.getOWLClass(farr(0), pm)
      val ind = df.getOWLNamedIndividual(farr(1), pm)
      val classAssertion = df.getOWLClassAssertionAxiom(cls, ind)
      manager.addAxiom(ontology, classAssertion)
    })

    val triplets=Source.fromFile("data/Triplets").getLines()
    triplets.foreach(f=>{
      val farr=f.split(",")
      val sub = df.getOWLNamedIndividual(farr(0), pm)

      if(farr(3)=="Obj")
       {
         val pred=df.getOWLObjectProperty(farr(1),pm)
          val obj=df.getOWLNamedIndividual(farr(2), pm)
          val objAsser = df.getOWLObjectPropertyAssertionAxiom(pred,sub, obj)
          manager.addAxiom(ontology, objAsser)
        }
      else if(farr(3)=="Data")
        {
          val pred=df.getOWLDataProperty(farr(1),pm)
          val dat=df.getOWLLiteral(farr(2))
          val datAsser = df.getOWLDataPropertyAssertionAxiom(pred,sub, dat)
          manager.addAxiom(ontology, datAsser)
        }
    })

    val os = new FileOutputStream("data/BreastCancerDiagnosis.owl")
    val owlxmlFormat = new OWLXMLDocumentFormat
    manager.saveOntology(ontology, owlxmlFormat, os)
    System.out.println("Ontology Created")



  }

}
