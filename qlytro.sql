
-- =====================================================================
-- SCHEMA: HE THONG QUAN LY PHONG TRO
-- Chi gom cau truc bang (CREATE TABLE), khong kem du lieu mau.
-- Dung Postman/Swagger de tao du lieu qua chinh cac API dang code.
-- Engine: InnoDB (bat buoc de ho tro khoa ngoai + transaction)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS quan_ly_phong_tro
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE quan_ly_phong_tro;

SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- 1. TOA_NHA
-- Khong co cot chu_tro_id vi he thong hien chi phuc vu 1 chu tro duy nhat.
-- Neu sau nay mo rong multi-tenant, chi can them cot nay vao day.
-- ---------------------------------------------------------------------
CREATE TABLE toa_nha (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    ten_toa_nha     VARCHAR(100) NOT NULL,
    dia_chi         VARCHAR(255) NULL,
    trang_thai      TINYINT NOT NULL DEFAULT 1
                    COMMENT '1 = HOAT_DONG, 0 = NGUNG_HOAT_DONG',
    ngay_tao        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. THIET_BI  (danh muc loai thiet bi: tu lanh, dieu hoa, nong lanh...)
-- ---------------------------------------------------------------------
CREATE TABLE thiet_bi (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    ten_thiet_bi    VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. NGUOI_DUNG
-- Dung chung cho ca Chu tro (vai_tro=1) va Khach thue (vai_tro=0).
-- password BAT BUOC luu dang hash (BCrypt) khi code Service, khong
-- bao gio luu plain text.
-- ---------------------------------------------------------------------
CREATE TABLE nguoi_dung (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL COMMENT 'Luu dang hash BCrypt',
    ho_ten          VARCHAR(100) NOT NULL,
    sdt             VARCHAR(15) NULL,
    email           VARCHAR(100) NULL,
    vai_tro         TINYINT NOT NULL COMMENT '1 = Chu tro, 0 = Nguoi thue',
    trang_thai      TINYINT NOT NULL DEFAULT 1
                    COMMENT '1 = active, 0 = khoa tai khoan',
    ngay_tao        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. PHONG
-- UNIQUE(toa_nha_id, so_phong): so phong chi can duy nhat trong 1 toa,
-- Toa A va Toa B deu co the co phong "101".
-- gia_thue_mac_dinh: gia niem yet, Service se COPY gia tri nay sang
-- hop_dong.tien_thue luc tao hop dong moi (khong tham chieu ngoc).
-- ---------------------------------------------------------------------
CREATE TABLE phong (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    toa_nha_id          INT NOT NULL,
    so_phong            VARCHAR(10) NOT NULL,
    dien_tich           DECIMAL(6,2) NULL,
    gia_thue_mac_dinh   DECIMAL(12,2) NOT NULL,
    trang_thai          TINYINT NOT NULL DEFAULT 0
                        COMMENT '0=TRONG, 1=DANG_THUE, 2=NGUNG_SU_DUNG',
    mo_ta               VARCHAR(255) NULL,
    CONSTRAINT fk_phong_toa_nha FOREIGN KEY (toa_nha_id)
        REFERENCES toa_nha(id),
    CONSTRAINT uq_phong_so_phong UNIQUE (toa_nha_id, so_phong)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. PHONG_THIET_BI (bang trung gian, quan he nhieu-nhieu)
-- Khong theo doi so luong, khong luu tinh trang o day (tinh trang
-- do module SU_CO quan ly toan bo de tranh trung lap/lech du lieu).
-- ---------------------------------------------------------------------
CREATE TABLE phong_thiet_bi (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    phong_id        INT NOT NULL,
    thiet_bi_id     INT NOT NULL,
    CONSTRAINT fk_ptb_phong FOREIGN KEY (phong_id) REFERENCES phong(id),
    CONSTRAINT fk_ptb_thiet_bi FOREIGN KEY (thiet_bi_id) REFERENCES thiet_bi(id),
    CONSTRAINT uq_ptb UNIQUE (phong_id, thiet_bi_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. KHACH_THUE
-- Luu THONG TIN CA NHAN CO DINH cua 1 nguoi (khong lap lai moi lan ky
-- hop dong moi). nguoi_dung_id NULLABLE: chi nguoi tung la dai dien
-- (co tai khoan) moi co gia tri; nguoi o cung khac de NULL.
-- ---------------------------------------------------------------------
CREATE TABLE khach_thue (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    ho_ten          VARCHAR(100) NOT NULL,
    cccd            VARCHAR(20) NOT NULL UNIQUE,
    ngay_sinh       DATE NULL,
    sdt             VARCHAR(15) NULL,
    nguoi_dung_id   INT NULL,
    CONSTRAINT fk_khach_thue_nguoi_dung FOREIGN KEY (nguoi_dung_id)
        REFERENCES nguoi_dung(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 7. HOP_DONG
-- Moi lan ky moi (ke ca gia han) la 1 dong MOI, khong sua de len hop
-- dong cu -> giu lich su tung ky rieng biet.
-- tien_thue: COPY tu phong.gia_thue_mac_dinh tai thoi diem tao, sau do
-- doc lap hoan toan (doi gia niem yet khong anh huong hop dong da ky).
-- ---------------------------------------------------------------------
CREATE TABLE hop_dong (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    phong_id            INT NOT NULL,
    ngay_bat_dau        DATE NOT NULL,
    ngay_ket_thuc       DATE NULL,
    tien_thue           DECIMAL(12,2) NOT NULL,
    tien_coc            DECIMAL(12,2) NOT NULL,
    tien_coc_con_lai    DECIMAL(12,2) NULL
                        COMMENT 'Chi co gia tri khi trang_thai_coc = 1, co the am neu cong no > coc',
    trang_thai          TINYINT NOT NULL DEFAULT 0
                        COMMENT '0=ACTIVE, 1=KET_THUC_BINH_THUONG, 2=KET_THUC_BAT_THUONG',
    trang_thai_coc      TINYINT NOT NULL DEFAULT 0
                        COMMENT '0=DANG_GIU, 1=DA_KHAU_TRU',
    ngay_tao            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hop_dong_phong FOREIGN KEY (phong_id) REFERENCES phong(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 8. HOP_DONG_KHACH_THUE (bang trung gian, quan he nhieu-nhieu theo
-- thoi gian: 1 hop dong nhieu nguoi o chung, 1 nguoi co the trai qua
-- nhieu hop dong khac nhau theo thoi gian)
-- la_dai_dien: nguoi dung tai khoan / dung ten chinh trong hop dong.
-- dang_o: 1 = dang o, 0 = da roi di (giu lai dong, khong xoa).
-- Khi doi dai dien: UPDATE truc tiep 2 dong hien co (tat dai dien cu,
-- bat dai dien moi), CHI insert dong moi khi co nguoi HOAN TOAN moi vao.
-- ---------------------------------------------------------------------
CREATE TABLE hop_dong_khach_thue (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    hop_dong_id     INT NOT NULL,
    khach_thue_id   INT NOT NULL,
    la_dai_dien     TINYINT NOT NULL DEFAULT 0,
    dang_o          TINYINT NOT NULL DEFAULT 1,
    CONSTRAINT fk_hdkt_hop_dong FOREIGN KEY (hop_dong_id) REFERENCES hop_dong(id),
    CONSTRAINT fk_hdkt_khach_thue FOREIGN KEY (khach_thue_id) REFERENCES khach_thue(id),
    CONSTRAINT uq_hdkt UNIQUE (hop_dong_id, khach_thue_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 9. CHI_SO_DIEN_NUOC
-- Gan voi PHONG (khong phai hop_dong) vi dong ho dien/nuoc la tai san
-- vat ly co dinh tai phong, khong "di chuyen" theo hop dong.
-- Luong xac nhan hang thang: khach bao (TAM_TINH) -> chu tro kiem tra
-- -> DA_CHOT (duoc phep tinh hoa don) hoac CAN_XAC_MINH (hoa don treo
-- lai cho phong nay den khi xac minh xong).
-- ---------------------------------------------------------------------
CREATE TABLE chi_so_dien_nuoc (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    phong_id            INT NOT NULL,
    thang_nam           VARCHAR(7) NOT NULL COMMENT 'VD: 2026-09',
    chi_so_dien_cu      DECIMAL(10,2) NOT NULL,
    chi_so_dien_moi     DECIMAL(10,2) NOT NULL,
    chi_so_nuoc_cu      DECIMAL(10,2) NOT NULL,
    chi_so_nuoc_moi     DECIMAL(10,2) NOT NULL,
    anh_chi_so          VARCHAR(255) NULL COMMENT 'Anh khach chup dong ho gui kem',
    trang_thai          TINYINT NOT NULL DEFAULT 0
                        COMMENT '0=TAM_TINH, 1=DA_CHOT, 2=CAN_XAC_MINH',
    ghi_chu             VARCHAR(255) NULL,
    ngay_bao            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ngay_chot           DATETIME NULL,
    CONSTRAINT fk_csdn_phong FOREIGN KEY (phong_id) REFERENCES phong(id),
    CONSTRAINT uq_csdn UNIQUE (phong_id, thang_nam)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 10. BANG_GIA_DIEN_NUOC
-- Luon chi co DUNG 1 dong (khong phai danh muc nhieu dong). Khong luu
-- lich su gia: doi gia la UPDATE truc tiep, khong anh huong hoa don cu
-- vi hoa don da COPY don_gia tai thoi diem tinh.
-- ---------------------------------------------------------------------
CREATE TABLE bang_gia_dien_nuoc (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    don_gia_dien    DECIMAL(10,2) NOT NULL COMMENT 'VD: 4000 (d/so)',
    don_gia_nuoc    DECIMAL(10,2) NOT NULL COMMENT 'VD: 35000 (d/khoi)',
    ngay_cap_nhat   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 11. PHI_DICH_VU
-- Danh muc phi co dinh, ap dung dong loat MOI phong (rac, internet,
-- gui xe...). dang_ap_dung=0 de tat 1 loai phi ma khong xoa lich su.
-- ---------------------------------------------------------------------
CREATE TABLE phi_dich_vu (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    ten_phi         VARCHAR(100) NOT NULL,
    so_tien         DECIMAL(10,2) NOT NULL,
    dang_ap_dung    TINYINT NOT NULL DEFAULT 1
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 12. SU_CO
-- Khach bao hong thiet bi. phong_id lay tu dong hop dong ACTIVE cua
-- tai khoan dang dang nhap (Service tu xac dinh, khong can khach chon).
-- nguyen_nhan chi duoc dien SAU khi chu tro xu ly xong (trang_thai=HOAN_THANH).
-- ---------------------------------------------------------------------
CREATE TABLE su_co (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    phong_id        INT NOT NULL,
    thiet_bi_id     INT NULL COMMENT 'NULL neu su co khong thuoc thiet bi cu the',
    nguoi_bao_id    INT NOT NULL,
    mo_ta           VARCHAR(255) NOT NULL,
    trang_thai      TINYINT NOT NULL DEFAULT 0
                    COMMENT '0=MOI, 1=DANG_XU_LY, 2=HOAN_THANH',
    nguyen_nhan     TINYINT NULL
                    COMMENT '0=HAO_MON_TU_NHIEN, 1=DO_NGUOI_O_LAM_HONG (dien sau khi HOAN_THANH)',
    ngay_bao        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_su_co_phong FOREIGN KEY (phong_id) REFERENCES phong(id),
    CONSTRAINT fk_su_co_thiet_bi FOREIGN KEY (thiet_bi_id) REFERENCES thiet_bi(id),
    CONSTRAINT fk_su_co_nguoi_bao FOREIGN KEY (nguoi_bao_id) REFERENCES nguoi_dung(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 13. BOI_THUONG_THIET_HAI
-- nguon_phat_hien quyet dinh y nghia trang_thai:
--   TRONG_LUC_O (0): luong thanh toan RIENG, doc lap hoa don thang
--     (0=CHUA_THANH_TOAN, 1=CHO_DUYET, 2=DA_THANH_TOAN)
--   LUC_TRA_PHONG (1): gop vao khau tru coc cuoi hop dong
--     (0=CHUA_XU_LY, 1=DA_KHAU_TRU_VAO_COC)
-- ---------------------------------------------------------------------
CREATE TABLE boi_thuong_thiet_hai (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    hop_dong_id         INT NOT NULL,
    su_co_id            INT NULL COMMENT 'Co gia tri neu phat sinh tu ticket bao hong',
    mo_ta               VARCHAR(255) NOT NULL,
    so_tien             DECIMAL(12,2) NOT NULL,
    nguon_phat_hien     TINYINT NOT NULL COMMENT '0=TRONG_LUC_O, 1=LUC_TRA_PHONG',
    trang_thai          TINYINT NOT NULL DEFAULT 0,
    anh_chuyen_khoan    VARCHAR(255) NULL COMMENT 'Chi dung khi nguon_phat_hien=TRONG_LUC_O',
    ngay_ghi_nhan       DATE NOT NULL,
    CONSTRAINT fk_bttv_hop_dong FOREIGN KEY (hop_dong_id) REFERENCES hop_dong(id),
    CONSTRAINT fk_bttv_su_co FOREIGN KEY (su_co_id) REFERENCES su_co(id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 14. HOA_DON
-- Gan voi HOP_DONG (khong phai phong) vi tien phong lay tu
-- hop_dong.tien_thue (gia rieng tung hop dong, co the khac gia niem yet).
-- Thanh toan 1 lan / thang, khong chia nho theo phan.
-- ---------------------------------------------------------------------
CREATE TABLE hoa_don (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    hop_dong_id         INT NOT NULL,
    thang_nam           VARCHAR(7) NOT NULL COMMENT 'VD: 2026-09',
    tong_tien           DECIMAL(12,2) NOT NULL,
    trang_thai          TINYINT NOT NULL DEFAULT 0
                        COMMENT '0=CHUA_THANH_TOAN, 1=CHO_DUYET, 2=DA_THANH_TOAN',
    anh_chuyen_khoan    VARCHAR(255) NULL,
    ngay_tao            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ngay_thanh_toan     DATETIME NULL,
    CONSTRAINT fk_hoa_don_hop_dong FOREIGN KEY (hop_dong_id) REFERENCES hop_dong(id),
    CONSTRAINT uq_hoa_don UNIQUE (hop_dong_id, thang_nam)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 15. HOA_DON_CHI_TIET
-- Tach rieng khoi HOA_DON de dung nguyen tac 3NF: them 1 loai phi moi
-- khong can sua cau truc bang. Day cung la cho the hien Strategy Pattern
-- khi code Service (moi loai_khoan ung voi 1 chien luoc tinh tien).
-- chi_so_dien_nuoc_id: chi de TRUY VET nguon goc so lieu, khong dung
-- de tinh toan (phep tru da xay ra san trong chi_so_dien_nuoc).
-- ---------------------------------------------------------------------
CREATE TABLE hoa_don_chi_tiet (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    hoa_don_id              INT NOT NULL,
    chi_so_dien_nuoc_id     INT NULL COMMENT 'Chi co gia tri voi dong TIEN_DIEN/TIEN_NUOC',
    loai_khoan              TINYINT NOT NULL
                            COMMENT '0=TIEN_PHONG, 1=TIEN_DIEN, 2=TIEN_NUOC, 3=PHI_DICH_VU',
    noi_dung                VARCHAR(255) NOT NULL,
    so_luong                DECIMAL(10,2) NULL COMMENT 'Chi dung cho dien/nuoc',
    don_gia                 DECIMAL(12,2) NULL COMMENT 'Chi dung cho dien/nuoc',
    thanh_tien              DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_hdct_hoa_don FOREIGN KEY (hoa_don_id) REFERENCES hoa_don(id),
    CONSTRAINT fk_hdct_chi_so FOREIGN KEY (chi_so_dien_nuoc_id) REFERENCES chi_so_dien_nuoc(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;