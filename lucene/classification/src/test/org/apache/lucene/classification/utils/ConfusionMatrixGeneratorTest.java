begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.classification.utils
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|MockAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|BM25NBClassifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|BooleanPerceptronClassifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|CachingNaiveBayesClassifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|ClassificationResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|ClassificationTestBase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|Classifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|KNearestFuzzyClassifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|KNearestNeighborClassifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
operator|.
name|SimpleNaiveBayesClassifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests for {@link ConfusionMatrixGenerator}  */
end_comment

begin_class
DECL|class|ConfusionMatrixGeneratorTest
specifier|public
class|class
name|ConfusionMatrixGeneratorTest
extends|extends
name|ClassificationTestBase
argument_list|<
name|Object
argument_list|>
block|{
annotation|@
name|Test
DECL|method|testGetConfusionMatrix
specifier|public
name|void
name|testGetConfusionMatrix
parameter_list|()
throws|throws
name|Exception
block|{
name|LeafReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|reader
operator|=
name|getSampleIndex
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|Classifier
argument_list|<
name|BytesRef
argument_list|>
name|classifier
init|=
operator|new
name|Classifier
argument_list|<
name|BytesRef
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
name|assignClass
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|ClassificationResult
argument_list|<>
argument_list|(
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|1
operator|/
operator|(
literal|1
operator|+
name|Math
operator|.
name|exp
argument_list|(
operator|-
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|getClasses
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|ClassificationResult
argument_list|<
name|BytesRef
argument_list|>
argument_list|>
name|getClasses
parameter_list|(
name|String
name|text
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|ConfusionMatrixGenerator
operator|.
name|ConfusionMatrix
name|confusionMatrix
init|=
name|ConfusionMatrixGenerator
operator|.
name|getConfusionMatrix
argument_list|(
name|reader
argument_list|,
name|classifier
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|confusionMatrix
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|confusionMatrix
operator|.
name|getLinearizedMatrix
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|confusionMatrix
operator|.
name|getNumberOfEvaluatedDocs
argument_list|()
argument_list|)
expr_stmt|;
name|double
name|avgClassificationTime
init|=
name|confusionMatrix
operator|.
name|getAvgClassificationTime
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|avgClassificationTime
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|double
name|accuracy
init|=
name|confusionMatrix
operator|.
name|getAccuracy
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|accuracy
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|accuracy
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|double
name|precision
init|=
name|confusionMatrix
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|precision
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|precision
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|double
name|recall
init|=
name|confusionMatrix
operator|.
name|getRecall
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|recall
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|recall
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|double
name|f1Measure
init|=
name|confusionMatrix
operator|.
name|getF1Measure
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|f1Measure
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f1Measure
operator|<=
literal|1d
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testGetConfusionMatrixWithSNB
specifier|public
name|void
name|testGetConfusionMatrixWithSNB
parameter_list|()
throws|throws
name|Exception
block|{
name|LeafReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|reader
operator|=
name|getSampleIndex
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|Classifier
argument_list|<
name|BytesRef
argument_list|>
name|classifier
init|=
operator|new
name|SimpleNaiveBayesClassifier
argument_list|(
name|reader
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|)
decl_stmt|;
name|ConfusionMatrixGenerator
operator|.
name|ConfusionMatrix
name|confusionMatrix
init|=
name|ConfusionMatrixGenerator
operator|.
name|getConfusionMatrix
argument_list|(
name|reader
argument_list|,
name|classifier
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|checkCM
argument_list|(
name|confusionMatrix
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|checkCM
specifier|private
name|void
name|checkCM
parameter_list|(
name|ConfusionMatrixGenerator
operator|.
name|ConfusionMatrix
name|confusionMatrix
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|confusionMatrix
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|confusionMatrix
operator|.
name|getLinearizedMatrix
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|confusionMatrix
operator|.
name|getNumberOfEvaluatedDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|confusionMatrix
operator|.
name|getAvgClassificationTime
argument_list|()
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|double
name|accuracy
init|=
name|confusionMatrix
operator|.
name|getAccuracy
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|accuracy
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|accuracy
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|double
name|precision
init|=
name|confusionMatrix
operator|.
name|getPrecision
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|precision
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|precision
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|double
name|recall
init|=
name|confusionMatrix
operator|.
name|getRecall
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|recall
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|recall
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|double
name|f1Measure
init|=
name|confusionMatrix
operator|.
name|getF1Measure
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|f1Measure
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f1Measure
operator|<=
literal|1d
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetConfusionMatrixWithBM25NB
specifier|public
name|void
name|testGetConfusionMatrixWithBM25NB
parameter_list|()
throws|throws
name|Exception
block|{
name|LeafReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|reader
operator|=
name|getSampleIndex
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|Classifier
argument_list|<
name|BytesRef
argument_list|>
name|classifier
init|=
operator|new
name|BM25NBClassifier
argument_list|(
name|reader
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|)
decl_stmt|;
name|ConfusionMatrixGenerator
operator|.
name|ConfusionMatrix
name|confusionMatrix
init|=
name|ConfusionMatrixGenerator
operator|.
name|getConfusionMatrix
argument_list|(
name|reader
argument_list|,
name|classifier
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|checkCM
argument_list|(
name|confusionMatrix
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testGetConfusionMatrixWithCNB
specifier|public
name|void
name|testGetConfusionMatrixWithCNB
parameter_list|()
throws|throws
name|Exception
block|{
name|LeafReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|reader
operator|=
name|getSampleIndex
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|Classifier
argument_list|<
name|BytesRef
argument_list|>
name|classifier
init|=
operator|new
name|CachingNaiveBayesClassifier
argument_list|(
name|reader
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|)
decl_stmt|;
name|ConfusionMatrixGenerator
operator|.
name|ConfusionMatrix
name|confusionMatrix
init|=
name|ConfusionMatrixGenerator
operator|.
name|getConfusionMatrix
argument_list|(
name|reader
argument_list|,
name|classifier
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|checkCM
argument_list|(
name|confusionMatrix
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testGetConfusionMatrixWithKNN
specifier|public
name|void
name|testGetConfusionMatrixWithKNN
parameter_list|()
throws|throws
name|Exception
block|{
name|LeafReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|reader
operator|=
name|getSampleIndex
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|Classifier
argument_list|<
name|BytesRef
argument_list|>
name|classifier
init|=
operator|new
name|KNearestNeighborClassifier
argument_list|(
name|reader
argument_list|,
literal|null
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|)
decl_stmt|;
name|ConfusionMatrixGenerator
operator|.
name|ConfusionMatrix
name|confusionMatrix
init|=
name|ConfusionMatrixGenerator
operator|.
name|getConfusionMatrix
argument_list|(
name|reader
argument_list|,
name|classifier
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|checkCM
argument_list|(
name|confusionMatrix
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testGetConfusionMatrixWithFLTKNN
specifier|public
name|void
name|testGetConfusionMatrixWithFLTKNN
parameter_list|()
throws|throws
name|Exception
block|{
name|LeafReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|reader
operator|=
name|getSampleIndex
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|Classifier
argument_list|<
name|BytesRef
argument_list|>
name|classifier
init|=
operator|new
name|KNearestFuzzyClassifier
argument_list|(
name|reader
argument_list|,
literal|null
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|)
decl_stmt|;
name|ConfusionMatrixGenerator
operator|.
name|ConfusionMatrix
name|confusionMatrix
init|=
name|ConfusionMatrixGenerator
operator|.
name|getConfusionMatrix
argument_list|(
name|reader
argument_list|,
name|classifier
argument_list|,
name|categoryFieldName
argument_list|,
name|textFieldName
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|checkCM
argument_list|(
name|confusionMatrix
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testGetConfusionMatrixWithBP
specifier|public
name|void
name|testGetConfusionMatrixWithBP
parameter_list|()
throws|throws
name|Exception
block|{
name|LeafReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|MockAnalyzer
name|analyzer
init|=
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|reader
operator|=
name|getSampleIndex
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|Classifier
argument_list|<
name|Boolean
argument_list|>
name|classifier
init|=
operator|new
name|BooleanPerceptronClassifier
argument_list|(
name|reader
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
name|booleanFieldName
argument_list|,
name|textFieldName
argument_list|)
decl_stmt|;
name|ConfusionMatrixGenerator
operator|.
name|ConfusionMatrix
name|confusionMatrix
init|=
name|ConfusionMatrixGenerator
operator|.
name|getConfusionMatrix
argument_list|(
name|reader
argument_list|,
name|classifier
argument_list|,
name|booleanFieldName
argument_list|,
name|textFieldName
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|checkCM
argument_list|(
name|confusionMatrix
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|confusionMatrix
operator|.
name|getPrecision
argument_list|(
literal|"true"
argument_list|)
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|confusionMatrix
operator|.
name|getPrecision
argument_list|(
literal|"true"
argument_list|)
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|confusionMatrix
operator|.
name|getPrecision
argument_list|(
literal|"false"
argument_list|)
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|confusionMatrix
operator|.
name|getPrecision
argument_list|(
literal|"false"
argument_list|)
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|confusionMatrix
operator|.
name|getRecall
argument_list|(
literal|"true"
argument_list|)
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|confusionMatrix
operator|.
name|getRecall
argument_list|(
literal|"true"
argument_list|)
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|confusionMatrix
operator|.
name|getRecall
argument_list|(
literal|"false"
argument_list|)
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|confusionMatrix
operator|.
name|getRecall
argument_list|(
literal|"false"
argument_list|)
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|confusionMatrix
operator|.
name|getF1Measure
argument_list|(
literal|"true"
argument_list|)
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|confusionMatrix
operator|.
name|getF1Measure
argument_list|(
literal|"true"
argument_list|)
operator|<=
literal|1d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|confusionMatrix
operator|.
name|getF1Measure
argument_list|(
literal|"false"
argument_list|)
operator|>=
literal|0d
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|confusionMatrix
operator|.
name|getF1Measure
argument_list|(
literal|"false"
argument_list|)
operator|<=
literal|1d
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

