begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.bbox
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|bbox
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
name|Explanation
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Rectangle
import|;
end_import

begin_comment
comment|/**  * The algorithm is implemented as envelope on envelope overlays rather than  * complex polygon on complex polygon overlays.  *<p/>  *<p/>  * Spatial relevance scoring algorithm:  *<p/>  *<br/>  queryArea = the area of the input query envelope  *<br/>  targetArea = the area of the target envelope (per Lucene document)  *<br/>  intersectionArea = the area of the intersection for the query/target envelopes  *<br/>  queryPower = the weighting power associated with the query envelope (default = 1.0)  *<br/>  targetPower =  the weighting power associated with the target envelope (default = 1.0)  *<p/>  *<br/>  queryRatio  = intersectionArea / queryArea;  *<br/>  targetRatio = intersectionArea / targetArea;  *<br/>  queryFactor  = Math.pow(queryRatio,queryPower);  *<br/>  targetFactor = Math.pow(targetRatio,targetPower);  *<br/>  score = queryFactor * targetFactor;  *<p/>  * Based on Geoportal's  *<a href="http://geoportal.svn.sourceforge.net/svnroot/geoportal/Geoportal/trunk/src/com/esri/gpt/catalog/lucene/SpatialRankingValueSource.java">  *   SpatialRankingValueSource</a>.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|AreaSimilarity
specifier|public
class|class
name|AreaSimilarity
implements|implements
name|BBoxSimilarity
block|{
comment|/**    * Properties associated with the query envelope    */
DECL|field|queryExtent
specifier|private
specifier|final
name|Rectangle
name|queryExtent
decl_stmt|;
DECL|field|queryArea
specifier|private
specifier|final
name|double
name|queryArea
decl_stmt|;
DECL|field|targetPower
specifier|private
specifier|final
name|double
name|targetPower
decl_stmt|;
DECL|field|queryPower
specifier|private
specifier|final
name|double
name|queryPower
decl_stmt|;
DECL|method|AreaSimilarity
specifier|public
name|AreaSimilarity
parameter_list|(
name|Rectangle
name|queryExtent
parameter_list|,
name|double
name|queryPower
parameter_list|,
name|double
name|targetPower
parameter_list|)
block|{
name|this
operator|.
name|queryExtent
operator|=
name|queryExtent
expr_stmt|;
name|this
operator|.
name|queryArea
operator|=
name|queryExtent
operator|.
name|getArea
argument_list|()
expr_stmt|;
name|this
operator|.
name|queryPower
operator|=
name|queryPower
expr_stmt|;
name|this
operator|.
name|targetPower
operator|=
name|targetPower
expr_stmt|;
comment|//  if (this.qryMinX> queryExtent.getMaxX()) {
comment|//    this.qryCrossedDateline = true;
comment|//    this.qryArea = Math.abs(qryMaxX + 360.0 - qryMinX) * Math.abs(qryMaxY - qryMinY);
comment|//  } else {
comment|//    this.qryArea = Math.abs(qryMaxX - qryMinX) * Math.abs(qryMaxY - qryMinY);
comment|//  }
block|}
DECL|method|AreaSimilarity
specifier|public
name|AreaSimilarity
parameter_list|(
name|Rectangle
name|queryExtent
parameter_list|)
block|{
name|this
argument_list|(
name|queryExtent
argument_list|,
literal|2.0
argument_list|,
literal|0.5
argument_list|)
expr_stmt|;
block|}
DECL|method|getDelimiterQueryParameters
specifier|public
name|String
name|getDelimiterQueryParameters
parameter_list|()
block|{
return|return
name|queryExtent
operator|.
name|toString
argument_list|()
operator|+
literal|";"
operator|+
name|queryPower
operator|+
literal|";"
operator|+
name|targetPower
return|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|double
name|score
parameter_list|(
name|Rectangle
name|target
parameter_list|,
name|Explanation
name|exp
parameter_list|)
block|{
if|if
condition|(
name|target
operator|==
literal|null
operator|||
name|queryArea
operator|<=
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|double
name|targetArea
init|=
name|target
operator|.
name|getArea
argument_list|()
decl_stmt|;
if|if
condition|(
name|targetArea
operator|<=
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|double
name|score
init|=
literal|0
decl_stmt|;
name|double
name|top
init|=
name|Math
operator|.
name|min
argument_list|(
name|queryExtent
operator|.
name|getMaxY
argument_list|()
argument_list|,
name|target
operator|.
name|getMaxY
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|bottom
init|=
name|Math
operator|.
name|max
argument_list|(
name|queryExtent
operator|.
name|getMinY
argument_list|()
argument_list|,
name|target
operator|.
name|getMinY
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|height
init|=
name|top
operator|-
name|bottom
decl_stmt|;
name|double
name|width
init|=
literal|0
decl_stmt|;
comment|// queries that cross the date line
if|if
condition|(
name|queryExtent
operator|.
name|getCrossesDateLine
argument_list|()
condition|)
block|{
comment|// documents that cross the date line
if|if
condition|(
name|target
operator|.
name|getCrossesDateLine
argument_list|()
condition|)
block|{
name|double
name|left
init|=
name|Math
operator|.
name|max
argument_list|(
name|queryExtent
operator|.
name|getMinX
argument_list|()
argument_list|,
name|target
operator|.
name|getMinX
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|right
init|=
name|Math
operator|.
name|min
argument_list|(
name|queryExtent
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|target
operator|.
name|getMaxX
argument_list|()
argument_list|)
decl_stmt|;
name|width
operator|=
name|right
operator|+
literal|360.0
operator|-
name|left
expr_stmt|;
block|}
else|else
block|{
name|double
name|qryWestLeft
init|=
name|Math
operator|.
name|max
argument_list|(
name|queryExtent
operator|.
name|getMinX
argument_list|()
argument_list|,
name|target
operator|.
name|getMaxX
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|qryWestRight
init|=
name|Math
operator|.
name|min
argument_list|(
name|target
operator|.
name|getMaxX
argument_list|()
argument_list|,
literal|180.0
argument_list|)
decl_stmt|;
name|double
name|qryWestWidth
init|=
name|qryWestRight
operator|-
name|qryWestLeft
decl_stmt|;
if|if
condition|(
name|qryWestWidth
operator|>
literal|0
condition|)
block|{
name|width
operator|=
name|qryWestWidth
expr_stmt|;
block|}
else|else
block|{
name|double
name|qryEastLeft
init|=
name|Math
operator|.
name|max
argument_list|(
name|target
operator|.
name|getMaxX
argument_list|()
argument_list|,
operator|-
literal|180.0
argument_list|)
decl_stmt|;
name|double
name|qryEastRight
init|=
name|Math
operator|.
name|min
argument_list|(
name|queryExtent
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|target
operator|.
name|getMaxX
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|qryEastWidth
init|=
name|qryEastRight
operator|-
name|qryEastLeft
decl_stmt|;
if|if
condition|(
name|qryEastWidth
operator|>
literal|0
condition|)
block|{
name|width
operator|=
name|qryEastWidth
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
comment|// queries that do not cross the date line
if|if
condition|(
name|target
operator|.
name|getCrossesDateLine
argument_list|()
condition|)
block|{
name|double
name|tgtWestLeft
init|=
name|Math
operator|.
name|max
argument_list|(
name|queryExtent
operator|.
name|getMinX
argument_list|()
argument_list|,
name|target
operator|.
name|getMinX
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|tgtWestRight
init|=
name|Math
operator|.
name|min
argument_list|(
name|queryExtent
operator|.
name|getMaxX
argument_list|()
argument_list|,
literal|180.0
argument_list|)
decl_stmt|;
name|double
name|tgtWestWidth
init|=
name|tgtWestRight
operator|-
name|tgtWestLeft
decl_stmt|;
if|if
condition|(
name|tgtWestWidth
operator|>
literal|0
condition|)
block|{
name|width
operator|=
name|tgtWestWidth
expr_stmt|;
block|}
else|else
block|{
name|double
name|tgtEastLeft
init|=
name|Math
operator|.
name|max
argument_list|(
name|queryExtent
operator|.
name|getMinX
argument_list|()
argument_list|,
operator|-
literal|180.0
argument_list|)
decl_stmt|;
name|double
name|tgtEastRight
init|=
name|Math
operator|.
name|min
argument_list|(
name|queryExtent
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|target
operator|.
name|getMaxX
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|tgtEastWidth
init|=
name|tgtEastRight
operator|-
name|tgtEastLeft
decl_stmt|;
if|if
condition|(
name|tgtEastWidth
operator|>
literal|0
condition|)
block|{
name|width
operator|=
name|tgtEastWidth
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|double
name|left
init|=
name|Math
operator|.
name|max
argument_list|(
name|queryExtent
operator|.
name|getMinX
argument_list|()
argument_list|,
name|target
operator|.
name|getMinX
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|right
init|=
name|Math
operator|.
name|min
argument_list|(
name|queryExtent
operator|.
name|getMaxX
argument_list|()
argument_list|,
name|target
operator|.
name|getMaxX
argument_list|()
argument_list|)
decl_stmt|;
name|width
operator|=
name|right
operator|-
name|left
expr_stmt|;
block|}
block|}
comment|// calculate the score
if|if
condition|(
operator|(
name|width
operator|>
literal|0
operator|)
operator|&&
operator|(
name|height
operator|>
literal|0
operator|)
condition|)
block|{
name|double
name|intersectionArea
init|=
name|width
operator|*
name|height
decl_stmt|;
name|double
name|queryRatio
init|=
name|intersectionArea
operator|/
name|queryArea
decl_stmt|;
name|double
name|targetRatio
init|=
name|intersectionArea
operator|/
name|targetArea
decl_stmt|;
name|double
name|queryFactor
init|=
name|Math
operator|.
name|pow
argument_list|(
name|queryRatio
argument_list|,
name|queryPower
argument_list|)
decl_stmt|;
name|double
name|targetFactor
init|=
name|Math
operator|.
name|pow
argument_list|(
name|targetRatio
argument_list|,
name|targetPower
argument_list|)
decl_stmt|;
name|score
operator|=
name|queryFactor
operator|*
name|targetFactor
operator|*
literal|10000.0
expr_stmt|;
if|if
condition|(
name|exp
operator|!=
literal|null
condition|)
block|{
comment|//        StringBuilder sb = new StringBuilder();
comment|//        sb.append("\nscore=").append(score);
comment|//        sb.append("\n  query=").append();
comment|//        sb.append("\n  target=").append(target.toString());
comment|//        sb.append("\n  intersectionArea=").append(intersectionArea);
comment|//
comment|//        sb.append(" queryArea=").append(queryArea).append(" targetArea=").append(targetArea);
comment|//        sb.append("\n  queryRatio=").append(queryRatio).append(" targetRatio=").append(targetRatio);
comment|//        sb.append("\n  queryFactor=").append(queryFactor).append(" targetFactor=").append(targetFactor);
comment|//        sb.append(" (queryPower=").append(queryPower).append(" targetPower=").append(targetPower).append(")");
name|exp
operator|.
name|setValue
argument_list|(
operator|(
name|float
operator|)
name|score
argument_list|)
expr_stmt|;
name|exp
operator|.
name|setDescription
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|Explanation
name|e
init|=
literal|null
decl_stmt|;
name|exp
operator|.
name|addDetail
argument_list|(
name|e
operator|=
operator|new
name|Explanation
argument_list|(
operator|(
name|float
operator|)
name|intersectionArea
argument_list|,
literal|"IntersectionArea"
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
operator|(
name|float
operator|)
name|width
argument_list|,
literal|"width; Query: "
operator|+
name|queryExtent
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
operator|(
name|float
operator|)
name|height
argument_list|,
literal|"height; Target: "
operator|+
name|target
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|exp
operator|.
name|addDetail
argument_list|(
name|e
operator|=
operator|new
name|Explanation
argument_list|(
operator|(
name|float
operator|)
name|queryFactor
argument_list|,
literal|"Query"
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
operator|(
name|float
operator|)
name|queryArea
argument_list|,
literal|"area"
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
operator|(
name|float
operator|)
name|queryRatio
argument_list|,
literal|"ratio"
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
operator|(
name|float
operator|)
name|queryPower
argument_list|,
literal|"power"
argument_list|)
argument_list|)
expr_stmt|;
name|exp
operator|.
name|addDetail
argument_list|(
name|e
operator|=
operator|new
name|Explanation
argument_list|(
operator|(
name|float
operator|)
name|targetFactor
argument_list|,
literal|"Target"
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
operator|(
name|float
operator|)
name|targetArea
argument_list|,
literal|"area"
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
operator|(
name|float
operator|)
name|targetRatio
argument_list|,
literal|"ratio"
argument_list|)
argument_list|)
expr_stmt|;
name|e
operator|.
name|addDetail
argument_list|(
operator|new
name|Explanation
argument_list|(
operator|(
name|float
operator|)
name|targetPower
argument_list|,
literal|"power"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|exp
operator|!=
literal|null
condition|)
block|{
name|exp
operator|.
name|setValue
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|exp
operator|.
name|setDescription
argument_list|(
literal|"Shape does not intersect"
argument_list|)
expr_stmt|;
block|}
return|return
name|score
return|;
block|}
comment|/**    * Determines if this ValueSource is equal to another.    *    * @param o the ValueSource to compare    * @return<code>true</code> if the two objects are based upon the same query envelope    */
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|AreaSimilarity
operator|.
name|class
condition|)
return|return
literal|false
return|;
name|AreaSimilarity
name|other
init|=
operator|(
name|AreaSimilarity
operator|)
name|o
decl_stmt|;
return|return
name|getDelimiterQueryParameters
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getDelimiterQueryParameters
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the ValueSource hash code.    *    * @return the hash code    */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getDelimiterQueryParameters
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

