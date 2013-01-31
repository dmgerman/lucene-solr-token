begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.demo.facet.simple
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|facet
operator|.
name|simple
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
name|analysis
operator|.
name|core
operator|.
name|WhitespaceAnalyzer
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
name|demo
operator|.
name|facet
operator|.
name|ExampleUtils
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Some definitions for the Simple Sample.  *   * @lucene.experimental  */
end_comment

begin_class
DECL|class|SimpleUtils
specifier|public
class|class
name|SimpleUtils
block|{
comment|/** No instance */
DECL|method|SimpleUtils
specifier|private
name|SimpleUtils
parameter_list|()
block|{}
comment|/**     * Documents text field.    */
DECL|field|TEXT
specifier|public
specifier|static
specifier|final
name|String
name|TEXT
init|=
literal|"text"
decl_stmt|;
comment|/**     * Documents title field.    */
DECL|field|TITLE
specifier|public
specifier|static
specifier|final
name|String
name|TITLE
init|=
literal|"title"
decl_stmt|;
comment|/**     * sample documents text (for the text field).    */
DECL|field|docTexts
specifier|public
specifier|static
name|String
index|[]
name|docTexts
init|=
block|{
literal|"the white car is the one I want."
block|,
literal|"the white dog does not belong to anyone."
block|,   }
decl_stmt|;
comment|/**     * sample documents titles (for the title field).    */
DECL|field|docTitles
specifier|public
specifier|static
name|String
index|[]
name|docTitles
init|=
block|{
literal|"white car"
block|,
literal|"white dog"
block|,   }
decl_stmt|;
comment|/**    * Categories: categories[D][N] == category-path no. N for document no. D.    */
DECL|field|categories
specifier|public
specifier|static
name|CategoryPath
index|[]
index|[]
name|categories
init|=
block|{
block|{
operator|new
name|CategoryPath
argument_list|(
literal|"root"
argument_list|,
literal|"a"
argument_list|,
literal|"f1"
argument_list|)
block|,
operator|new
name|CategoryPath
argument_list|(
literal|"root"
argument_list|,
literal|"a"
argument_list|,
literal|"f2"
argument_list|)
block|}
block|,
block|{
operator|new
name|CategoryPath
argument_list|(
literal|"root"
argument_list|,
literal|"a"
argument_list|,
literal|"f1"
argument_list|)
block|,
operator|new
name|CategoryPath
argument_list|(
literal|"root"
argument_list|,
literal|"a"
argument_list|,
literal|"f3"
argument_list|)
block|}
block|,   }
decl_stmt|;
comment|/**    * Analyzer used in the simple sample.    */
DECL|field|analyzer
specifier|public
specifier|static
specifier|final
name|Analyzer
name|analyzer
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|ExampleUtils
operator|.
name|EXAMPLE_VER
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

