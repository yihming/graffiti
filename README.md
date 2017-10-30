# Graffiti
A collection of my trial projects with new skills or just for fun.

## Deep-Learning

A set of deep learning model implementation with training on realistic problems.

### Gesture

#### Work done:

* Training set consists of 1080 images of 64*64 pixels, with labels the numbers by which the gestures in the images represent. (in _gesture/datastes/_)
* Testing set consists of 131 images (also in _gesture/datasets/_), as well as photos of my own in _images_.
* Developed a DNN model with 3 layers for the multivariate classification:
  * Using Google TensorFlow by Python3
  * Training with Adam optimizer, which took about 10 minutes
  * Achieved 99% correction rate on the training set, and 71% on the testing set.

#### Next steps:

* Implement a CNN model to resolve the possible overfitting issue in DNN, and thus improve the testing rate.
* Implement both models using MXNET.

## BNU Miner

An online data mining platform.

#### Implementation:

* Database
  * MySQL. Construct the tables required by importing _bnuminer.sql_ to the database.
  * Managing the user profiles for login verification and permission.
* Front-end
  * Use Java Servlet with Google Web Toolkit (GWT) to construct a Web 2.0 AJAX web application.
  * Try with Adobe Flex to implement a drag-and-drop GUI to build up a data mining workflow.
  * Separation of client-end and server-end.
  * Run in Tomcat server.
* Back-end
  * Based on the source code of __WEKA__ (an open-source machine learning algorithm tool developed at University of Waikato, New Zealand). You can download it [here](https://www.cs.waikato.ac.nz/ml/weka/)
  * Use Java to glue the front-end, back-end and the database operations.

#### Next steps:

* When I developed this platform, and gave a presentation in a group, it was criticized that I didn't put effort on the concern of scalability. At that time, I couldn't do much because I knew nothing about this area. But now, I'm able to try it on by deploying the project on some cloud computing system.

## Geolife

A class project trying to discover interesting features or applications on an open time-series dataset.

* Dataset
  * A collection of 182 drivers in Beijing, China, from April 2007 to August 2012. It was collected by Microsoft Research Asia, and is open for [downloading](https://www.microsoft.com/en-us/download/details.aspx?id=52367&from=https%3A%2F%2Fresearch.microsoft.com%2Fen-us%2Fdownloads%2Fb16d359d-d164-469e-9fd4-daa38f2b2e13%2F)
  * The data dimension description, as well as some statistic summaries, can be read in the pdf file inside the data package downloaded.
* Goals:
  * Distinguish the local drivers from travellers, based on the analysis on their daily/weekly GPS trajectories.
  * For a new driver, build a recommendation system by selecting other drivers of the same kind (local or traveller), computing the similarity using Pearson's Correlation, and providing recommended sites.

## Flatworld

A small computer game, with users' strategies using Neural Networks to win.

* Game Settings:
  * A 2D bounded world with 3 kinds of food scattered: one healthy (+HP), one neutral, and one poisson (-HP).
  * An agent starts at somewhere, trying to live as long as possible by moving and eating food.
  * Agent's HP decreases constantly as the time goes.
  * Agent's has eyes like scanning rays with distance bounded, and has ears hearing the different sound from the food from a distance.
  * Agent dies when its HP becomes 0.
* Strategy:
  * Construct one NN model for eyes, and another for ears.
  * Correct the weights after eating one food and check the change of HP.
  * Construct another data structure to memorize the food already seen/heard but passed, so that it can choose the nearest healthy food, not always those in front of its eyes/ears.
* Result:
  * Please read the pdf report for details.

## Meta-Math

A semantics injection tool to process the physics quantity and unit information in the comment lines to the compiler for verification. Moreover, it is able to translate languages between C++, Java, and Matlab.

* Goals:
  * For scientific functions, be able to statically check the type safety not only on primitive data types, but on physics quantities and units as well.
* Domain Specific Language (DSL)
  * Designed for scientific programs written in C++ or Matlab.
  * Capture physics quantity and unit information of the parameters and return type for each function.
* Prototype Implementation
  * Use ANTLR and StringTemplate as the Front-end: read the source program, process the DSL information, generate the abstract syntax tree (AST).
  * Use Graphviz to plot the AST for checking.

#### Next Steps:

* Perform the basic dimensional analysis on unit.
  

## Sauce Code

A compiler for a simply-typed lambda calculus language. A semester-long class project for Advanced Compiler Construction course working with 2 other team members.

#### Features

* DeBruijn transformation.
* Small-step evaluation; Natural evaluation.
* 3 different Abstract Machines implementation on evaluations.
* Continuation-Passing Style (CPS) transformation on the intermediate representation.

Implemented using Haskell.

## Git DFS

An implementation on a simple Distributed File System (DFS) using Git, written in Python. A class project for Advanced Operating System course with another team member.

## Computational Geometry

A collection of my implementations on computational geometry algorithms.

* Delaunay
  * Implement the Delaunay Triangulation on any given convex polygon.
  * Written in C++, plotted using GNUPlot.

## AS

An OA system.

* Features:
  * User profile; photo upload; user group management; conference management; meeting room management; message communication system.
* Implementation:
  * Database: Oracle.
  * Using Java Servlet for back-end and database operation.
  * Front-end: jQuery
