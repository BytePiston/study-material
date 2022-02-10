1. Check whether a directory with the name `.kube` exists in your home directory.
    If not, create it.
1. Check whether the `.kube` directory contains a file named `config`.
    If it does, rename it to something else, e.g. `config-old`.
1. Download the cluster config from the [Gardener Dashboard](https://dashboard.garden.canary.k8s.ondemand.com/):
    1. Click on your cluster.
    2. In the `Access` section, there is a row `Kubeconfig`.
    Click on the download icon (Download Kubeconfig). Note: It may take some time until the `Kubeconfig` is available on cluster creation.
1. Copy the downloaded file to `~/.kube/config`.
1. Verify that the config has been applied correctly by running the following command:
    ```shell
    kubectl cluster-info
    ```

    It should print a message similar to the following, with `<CLUSTER>` and `<PROJECT>` being replaced:
    ```
    Kubernetes control plane is running at https://api.<CLUSTER>.<PROJECT>.shoot.canary.k8s-hana.ondemand.com
    CoreDNS is running at https://api.<CLUSTER>.<PROJECT>.shoot.canary.k8s-hana.ondemand.com/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy
    ```

1. Remember the `<CLUSTER>` and `<PROJECT>` names (or the fact that you can get them using the above command), you will need them later.
