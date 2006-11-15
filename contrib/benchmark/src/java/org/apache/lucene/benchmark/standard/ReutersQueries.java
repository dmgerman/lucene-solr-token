begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|standard
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
name|search
operator|.
name|spans
operator|.
name|SpanQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanNearQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanTermQuery
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
name|search
operator|.
name|spans
operator|.
name|SpanFirstQuery
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|WildcardQuery
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
name|Term
import|;
end_import

begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|ReutersQueries
specifier|public
class|class
name|ReutersQueries
block|{
DECL|field|STANDARD_QUERIES
specifier|public
specifier|static
name|String
index|[]
name|STANDARD_QUERIES
init|=
block|{
comment|//Start with some short queries
literal|"Salomon"
block|,
literal|"Comex"
block|,
literal|"night trading"
block|,
literal|"Japan Sony"
block|,
comment|//Try some Phrase Queries
literal|"\"Sony Japan\""
block|,
literal|"\"food needs\"~3"
block|,
literal|"\"World Bank\"^2 AND Nigeria"
block|,
literal|"\"World Bank\" -Nigeria"
block|,
literal|"\"Ford Credit\"~5"
block|,
comment|//Try some longer queries
literal|"airline Europe Canada destination"
block|,
literal|"Long term pressure by trade "
operator|+
literal|"ministers is necessary if the current Uruguay round of talks on "
operator|+
literal|"the General Agreement on Trade and Tariffs (GATT) is to "
operator|+
literal|"succeed"
block|}
decl_stmt|;
DECL|method|getPrebuiltQueries
specifier|public
specifier|static
name|Query
index|[]
name|getPrebuiltQueries
parameter_list|(
name|String
name|field
parameter_list|)
block|{
comment|//be wary of unanalyzed text
return|return
operator|new
name|Query
index|[]
block|{
operator|new
name|SpanFirstQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"ford"
argument_list|)
argument_list|)
argument_list|,
literal|5
argument_list|)
block|,
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"night"
argument_list|)
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"trading"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|4
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|SpanNearQuery
argument_list|(
operator|new
name|SpanQuery
index|[]
block|{
operator|new
name|SpanFirstQuery
argument_list|(
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"ford"
argument_list|)
argument_list|)
argument_list|,
literal|10
argument_list|)
block|,
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"credit"
argument_list|)
argument_list|)
block|}
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
block|,
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
literal|"fo*"
argument_list|)
argument_list|)
block|,         }
return|;
block|}
block|}
end_class

end_unit

