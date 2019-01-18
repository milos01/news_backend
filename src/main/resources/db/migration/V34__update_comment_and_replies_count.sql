UPDATE content AS Content INNER JOIN (
  SELECT count(Cmt.content_id) AS Count, Cnt.id AS ContentId  FROM comment AS Cmt INNER JOIN content AS Cnt ON Cmt.content_id = Cnt.id GROUP BY Cnt.id
) AS cntGroup ON cntGroup.ContentId = Content.id
SET Content.r_comments = cntGroup.Count;