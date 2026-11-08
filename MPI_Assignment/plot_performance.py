
import csv
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

processes = []
times = []

with open("mpi_performance.csv", newline="") as file:
    reader = csv.DictReader(file)
    for row in reader:
        processes.append(int(row["Processes"]))
        times.append(float(row["Average_Seconds"]))

plt.figure(figsize=(8, 5))
plt.plot(processes, times, marker="o", linewidth=2)

plt.title("MPI Processes vs. Average Execution Time")
plt.xlabel("Number of MPI Processes")
plt.ylabel("Average Execution Time (seconds)")
plt.xticks(processes)
plt.grid(True, alpha=0.3)

for x, y in zip(processes, times):
    plt.annotate(
        f"{y:.3f}s",
        (x, y),
        xytext=(0, 10),
        textcoords="offset points",
        ha="center"
    )

plt.tight_layout()
plt.savefig("mpi_performance.png", dpi=300)
print("Graph saved as mpi_performance.png")
