EuroVocMLC
==========

A Multi-Label Classifier for Eurovoc dataset based on Learning to Rank approach


# Usage
This project is developed in Java. You need Apache Maven to compile the sources.

You need to set all the paths to currect files in MLC/src/main/resources/Config.properties file. After setting all paths, you can compile the project using Maven. The jar files will be created in MLC/target folder. 

To build the feature vectors from scratch, you first need to index the collection of documents and build indexes of documents and classes. To index the documents and classes, you need to run MLC/src/main/java/nl/uva/mlc/eurovoc/irengine/Indexer.java. This will use Lucene indexing tool to create the indexes. 

After creating indexes of documents and classes, you can run MLC/src/main/java/nl/uva/mlc/eurovoc/featureextractor/main.java to create the feature vectors. 

The feature vectors will be created in [L2R format](https://www.cs.cornell.edu/people/tj/svm_light/svm_rank.html). The feature vectors are automatically divided into K (default value is 5) folds and can be fed to a learning to rank algorithm to create a ranking model.

# Dependencies

1. Lucene 4.0
2. Apache Maven (>3)


