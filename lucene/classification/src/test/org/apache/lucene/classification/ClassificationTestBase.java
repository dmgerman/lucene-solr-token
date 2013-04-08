begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.classification
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|classification
package|;
end_package

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
name|Analyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FieldType
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
name|document
operator|.
name|TextField
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
name|RandomIndexWriter
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
name|SlowCompositeReaderWrapper
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
name|store
operator|.
name|Directory
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
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_comment
comment|/**  * Base class for testing {@link Classifier}s  */
end_comment

begin_class
DECL|class|ClassificationTestBase
specifier|public
specifier|abstract
class|class
name|ClassificationTestBase
parameter_list|<
name|T
parameter_list|>
extends|extends
name|LuceneTestCase
block|{
DECL|field|indexWriter
specifier|private
name|RandomIndexWriter
name|indexWriter
decl_stmt|;
DECL|field|textFieldName
specifier|private
name|String
name|textFieldName
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|categoryFieldName
name|String
name|categoryFieldName
decl_stmt|;
DECL|field|booleanFieldName
name|String
name|booleanFieldName
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|indexWriter
operator|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|textFieldName
operator|=
literal|"text"
expr_stmt|;
name|categoryFieldName
operator|=
literal|"cat"
expr_stmt|;
name|booleanFieldName
operator|=
literal|"bool"
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|checkCorrectClassification
specifier|protected
name|void
name|checkCorrectClassification
parameter_list|(
name|Classifier
argument_list|<
name|T
argument_list|>
name|classifier
parameter_list|,
name|T
name|expectedResult
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|classFieldName
parameter_list|)
throws|throws
name|Exception
block|{
name|SlowCompositeReaderWrapper
name|compositeReaderWrapper
init|=
literal|null
decl_stmt|;
try|try
block|{
name|populateIndex
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|compositeReaderWrapper
operator|=
operator|new
name|SlowCompositeReaderWrapper
argument_list|(
name|indexWriter
operator|.
name|getReader
argument_list|()
argument_list|)
expr_stmt|;
name|classifier
operator|.
name|train
argument_list|(
name|compositeReaderWrapper
argument_list|,
name|textFieldName
argument_list|,
name|classFieldName
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|String
name|newText
init|=
literal|"Much is made of what the likes of Facebook, Google and Apple know about users. Truth is, Amazon may know more."
decl_stmt|;
name|ClassificationResult
argument_list|<
name|T
argument_list|>
name|classificationResult
init|=
name|classifier
operator|.
name|assignClass
argument_list|(
name|newText
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|classificationResult
operator|.
name|getAssignedClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"got an assigned class of "
operator|+
name|classificationResult
operator|.
name|getAssignedClass
argument_list|()
argument_list|,
name|expectedResult
argument_list|,
name|classificationResult
operator|.
name|getAssignedClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got a not positive score "
operator|+
name|classificationResult
operator|.
name|getScore
argument_list|()
argument_list|,
name|classificationResult
operator|.
name|getScore
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|compositeReaderWrapper
operator|!=
literal|null
condition|)
name|compositeReaderWrapper
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|populateIndex
specifier|private
name|void
name|populateIndex
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|Exception
block|{
name|FieldType
name|ft
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|ft
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ft
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
literal|"The traveling press secretary for Mitt Romney lost his cool and cursed at reporters "
operator|+
literal|"who attempted to ask questions of the Republican presidential candidate in a public plaza near the Tomb of "
operator|+
literal|"the Unknown Soldier in Warsaw Tuesday."
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"politics"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"false"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
literal|"Mitt Romney seeks to assure Israel and Iran, as well as Jewish voters in the United"
operator|+
literal|" States, that he will be tougher against Iran's nuclear ambitions than President Barack Obama."
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"politics"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"false"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
literal|"And there's a threshold question that he has to answer for the American people and "
operator|+
literal|"that's whether he is prepared to be commander-in-chief,\" she continued. \"As we look to the past events, we "
operator|+
literal|"know that this raises some questions about his preparedness and we'll see how the rest of his trip goes.\""
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"politics"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"false"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
literal|"Still, when it comes to gun policy, many congressional Democrats have \"decided to "
operator|+
literal|"keep quiet and not go there,\" said Alan Lizotte, dean and professor at the State University of New York at "
operator|+
literal|"Albany's School of Criminal Justice."
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"politics"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"false"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
literal|"Standing amongst the thousands of people at the state Capitol, Jorstad, director of "
operator|+
literal|"technology at the University of Wisconsin-La Crosse, documented the historic moment and shared it with the "
operator|+
literal|"world through the Internet."
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"technology"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"true"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
literal|"So, about all those experts and analysts who've spent the past year or so saying "
operator|+
literal|"Facebook was going to make a phone. A new expert has stepped forward to say it's not going to happen."
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"technology"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"true"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|textFieldName
argument_list|,
literal|"More than 400 million people trust Google with their e-mail, and 50 million store files"
operator|+
literal|" in the cloud using the Dropbox service. People manage their bank accounts, pay bills, trade stocks and "
operator|+
literal|"generally transfer or store huge volumes of personal data online."
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|categoryFieldName
argument_list|,
literal|"technology"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|booleanFieldName
argument_list|,
literal|"true"
argument_list|,
name|ft
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

