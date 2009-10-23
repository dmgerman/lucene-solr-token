begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.stats
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|stats
package|;
end_package

begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * This class holds series of TimeData related to a single test run. TimeData  * values may contribute to different measurements, so this class provides also  * some useful methods to separate them.  *  */
end_comment

begin_class
DECL|class|TestRunData
specifier|public
class|class
name|TestRunData
block|{
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
comment|/** Start and end time of this test run. */
DECL|field|start
DECL|field|end
specifier|private
name|long
name|start
init|=
literal|0L
decl_stmt|,
name|end
init|=
literal|0L
decl_stmt|;
DECL|field|data
specifier|private
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Vector
argument_list|<
name|TimeData
argument_list|>
argument_list|>
name|data
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Vector
argument_list|<
name|TimeData
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|TestRunData
specifier|public
name|TestRunData
parameter_list|()
block|{}
DECL|method|TestRunData
specifier|public
name|TestRunData
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|getData
specifier|public
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Vector
argument_list|<
name|TimeData
argument_list|>
argument_list|>
name|getData
parameter_list|()
block|{
return|return
name|data
return|;
block|}
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|setId
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|getEnd
specifier|public
name|long
name|getEnd
parameter_list|()
block|{
return|return
name|end
return|;
block|}
DECL|method|getStart
specifier|public
name|long
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
comment|/** Mark the starting time of this test run. */
DECL|method|startRun
specifier|public
name|void
name|startRun
parameter_list|()
block|{
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
comment|/** Mark the ending time of this test run. */
DECL|method|endRun
specifier|public
name|void
name|endRun
parameter_list|()
block|{
name|end
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
comment|/** Add a data point. */
DECL|method|addData
specifier|public
name|void
name|addData
parameter_list|(
name|TimeData
name|td
parameter_list|)
block|{
name|td
operator|.
name|recordMemUsage
argument_list|()
expr_stmt|;
name|Vector
argument_list|<
name|TimeData
argument_list|>
name|v
init|=
name|data
operator|.
name|get
argument_list|(
name|td
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|v
operator|=
operator|new
name|Vector
argument_list|<
name|TimeData
argument_list|>
argument_list|()
expr_stmt|;
name|data
operator|.
name|put
argument_list|(
name|td
operator|.
name|name
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
name|v
operator|.
name|add
argument_list|(
operator|(
name|TimeData
operator|)
name|td
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Get a list of all available types of data points. */
DECL|method|getLabels
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getLabels
parameter_list|()
block|{
return|return
name|data
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/** Get total values from all data points of a given type. */
DECL|method|getTotals
specifier|public
name|TimeData
name|getTotals
parameter_list|(
name|String
name|label
parameter_list|)
block|{
name|Vector
argument_list|<
name|TimeData
argument_list|>
name|v
init|=
name|data
operator|.
name|get
argument_list|(
name|label
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TimeData
name|res
init|=
operator|new
name|TimeData
argument_list|(
literal|"TOTAL "
operator|+
name|label
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|v
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TimeData
name|td
init|=
name|v
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|res
operator|.
name|count
operator|+=
name|td
operator|.
name|count
expr_stmt|;
name|res
operator|.
name|elapsed
operator|+=
name|td
operator|.
name|elapsed
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
comment|/** Get total values from all data points of all types.    * @return a list of TimeData values for all types.    */
DECL|method|getTotals
specifier|public
name|Vector
argument_list|<
name|TimeData
argument_list|>
name|getTotals
parameter_list|()
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|labels
init|=
name|getLabels
argument_list|()
decl_stmt|;
name|Vector
argument_list|<
name|TimeData
argument_list|>
name|v
init|=
operator|new
name|Vector
argument_list|<
name|TimeData
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|labels
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|TimeData
name|td
init|=
name|getTotals
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|v
operator|.
name|add
argument_list|(
name|td
argument_list|)
expr_stmt|;
block|}
return|return
name|v
return|;
block|}
comment|/** Get memory usage stats for a given data type. */
DECL|method|getMemUsage
specifier|public
name|MemUsage
name|getMemUsage
parameter_list|(
name|String
name|label
parameter_list|)
block|{
name|Vector
argument_list|<
name|TimeData
argument_list|>
name|v
init|=
name|data
operator|.
name|get
argument_list|(
name|label
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|MemUsage
name|res
init|=
operator|new
name|MemUsage
argument_list|()
decl_stmt|;
name|res
operator|.
name|minFree
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
name|res
operator|.
name|minTotal
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
name|long
name|avgFree
init|=
literal|0L
decl_stmt|,
name|avgTotal
init|=
literal|0L
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|v
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TimeData
name|td
init|=
name|v
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|.
name|maxFree
operator|<
name|td
operator|.
name|freeMem
condition|)
block|{
name|res
operator|.
name|maxFree
operator|=
name|td
operator|.
name|freeMem
expr_stmt|;
block|}
if|if
condition|(
name|res
operator|.
name|maxTotal
operator|<
name|td
operator|.
name|totalMem
condition|)
block|{
name|res
operator|.
name|maxTotal
operator|=
name|td
operator|.
name|totalMem
expr_stmt|;
block|}
if|if
condition|(
name|res
operator|.
name|minFree
operator|>
name|td
operator|.
name|freeMem
condition|)
block|{
name|res
operator|.
name|minFree
operator|=
name|td
operator|.
name|freeMem
expr_stmt|;
block|}
if|if
condition|(
name|res
operator|.
name|minTotal
operator|>
name|td
operator|.
name|totalMem
condition|)
block|{
name|res
operator|.
name|minTotal
operator|=
name|td
operator|.
name|totalMem
expr_stmt|;
block|}
name|avgFree
operator|+=
name|td
operator|.
name|freeMem
expr_stmt|;
name|avgTotal
operator|+=
name|td
operator|.
name|totalMem
expr_stmt|;
block|}
name|res
operator|.
name|avgFree
operator|=
name|avgFree
operator|/
name|v
operator|.
name|size
argument_list|()
expr_stmt|;
name|res
operator|.
name|avgTotal
operator|=
name|avgTotal
operator|/
name|v
operator|.
name|size
argument_list|()
expr_stmt|;
return|return
name|res
return|;
block|}
comment|/** Return a string representation. */
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|label
range|:
name|getLabels
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|id
argument_list|)
operator|.
name|append
argument_list|(
literal|"-"
argument_list|)
operator|.
name|append
argument_list|(
name|label
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|getTotals
argument_list|(
name|label
argument_list|)
operator|.
name|toString
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getMemUsage
argument_list|(
name|label
argument_list|)
operator|.
name|toScaledString
argument_list|(
literal|1024
operator|*
literal|1024
argument_list|,
literal|"MB"
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

