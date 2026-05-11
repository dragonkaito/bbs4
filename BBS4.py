import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

# =========================
# 1. データ読み込み
# =========================
df = pd.read_csv("bbs5_risk_java.csv", header=None)
df.columns = ["seed", "period"]

print("データ数:", len(df))
print(df.head())

# =========================
# 2. 基本統計量
# =========================
mean = np.mean(df["period"])
var = np.var(df["period"])
std = np.std(df["period"])

print("=== 統計量 ===")
print("平均:", mean)
print("分散:", var)
print("標準偏差:", std)

# =========================
# 追加：最大値・最小値
# =========================

max_period = df["period"].max()
min_period = df["period"].min()

print("最大周期:", max_period)
print("最小周期:", min_period)

# どのseedで出たか
max_row = df[df["period"] == max_period]
min_row = df[df["period"] == min_period]

print("\n最大周期のseed:")
print(max_row)

print("\n最小周期のseed:")
print(min_row)

# =========================
# 3. ヒストグラム
# =========================
plt.figure()
plt.hist(df["period"], bins=30)
plt.title("Period Distribution")
plt.xlabel("Period Length")
plt.ylabel("Frequency")
plt.grid()
plt.savefig('histogram.png')
plt.show()

# =========================
# 4. 箱ひげ図
# =========================
plt.figure()
plt.boxplot(df["period"])
plt.title("Boxplot of Period Length")
plt.ylabel("Period")
plt.grid()
plt.savefig('boxplot.png')
plt.show()

# =========================
# 5. 散布図（seed vs period）
# =========================
plt.figure()
plt.scatter(df["seed"], df["period"])
plt.title("Seed vs Period")
plt.xlabel("Seed")
plt.ylabel("Period")
plt.grid()
plt.savefig('scatter.png')
plt.show()