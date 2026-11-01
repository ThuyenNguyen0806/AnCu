INSERT INTO NhaO(code,name,type,address,area,price,status,rooms,image,description) VALUES
('BT-001','The Garden Residence','VILLA','Thảo Điền, TP. Hồ Chí Minh',320,12800000000,'Sẵn bán',4,0,'Biệt thự sân vườn với không gian mở, hồ bơi riêng.'),
('ND-002','Ocean Breeze Retreat','RESORT','Mũi Né, Bình Thuận',580,18500000000,'Sẵn bán',8,1,'Không gian nghỉ dưỡng gần biển, nhiều tiện ích thư giãn.'),
('BT-003','An Nhiên Villa','VILLA','Hòa Xuân, Đà Nẵng',250,8700000000,'Đã đặt chỗ',3,2,'Biệt thự ba phòng ngủ, sân vườn xanh mát.'),
('BT-004','Green Valley House','VILLA','Đà Lạt, Lâm Đồng',400,15200000000,'Sẵn bán',5,3,'Biệt thự đồi thông với sân vườn rộng rãi.'),
('ND-005','Serenity Riverside','RESORT','Cẩm Thanh, Hội An',720,24000000000,'Sẵn bán',12,4,'Khu nghỉ dưỡng ven sông với nhiều tiện ích.'),
('BT-006','The Palm Villa','VILLA','An Phú, TP. Hồ Chí Minh',285,10900000000,'Đã bán',4,5,'Biệt thự hiện đại có sân vườn và hồ bơi.')
ON CONFLICT (code) DO NOTHING;
INSERT INTO BietThuNhaVuon(houseId,garden,pool)
SELECT n.id,v.garden,v.pool FROM NhaO n
JOIN (VALUES ('BT-001',120,35),('BT-003',80,24),('BT-004',160,40),('BT-006',95,30)) AS v(code,garden,pool)
ON n.code=v.code WHERE n.type='VILLA'
ON CONFLICT (houseId) DO NOTHING;
INSERT INTO KhuNghiDuong(houseId,amenities)
SELECT n.id,v.amenities FROM NhaO n
JOIN (VALUES ('ND-002',6),('ND-005',9)) AS v(code,amenities)
ON n.code=v.code WHERE n.type='RESORT'
ON CONFLICT (houseId) DO NOTHING;
